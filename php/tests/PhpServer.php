<?php

namespace Lacuna\Tests;

/**
 * A php -S process for the tests, on a free port of 127.0.0.1. What it writes to the console goes
 * to a file, which output() reads.
 */
class PhpServer
{
    private $process;
    private $logFile;
    public $url;

    // Starts "php -S 127.0.0.1:<port> <router>" in the folder given, with these environment
    // variables added to the tests' own. An IP address, not "localhost": the port is checked free
    // on 127.0.0.1, and "localhost" could bind ::1 instead.
    public function __construct($folder, $router, array $environment)
    {
        $port = self::freePort();
        $this->url = "http://127.0.0.1:$port";
        $this->logFile = tempnam(sys_get_temp_dir(), 'php-server');
        $command = [PHP_BINARY, '-S', "127.0.0.1:$port", $router];
        $environment = array_merge(getenv(), $environment);
        // With workers, php -S serves several requests at once; the tests rely on one at a time.
        unset($environment['PHP_CLI_SERVER_WORKERS']);
        $this->process = proc_open(
            self::commandLine($command),
            [0 => ['file', self::nullDevice(), 'r'], 1 => ['file', $this->logFile, 'a'], 2 => ['file', $this->logFile, 'a']],
            $pipes,
            $folder,
            $environment,
            // On Windows, start php itself, not cmd.exe, so that proc_terminate stops php.
            ['bypass_shell' => true]
        );
        if (!is_resource($this->process)) {
            unlink($this->logFile);
            throw new \RuntimeException('Cannot start php -S');
        }
        try {
            $this->waitUntilListening($port);
        } catch (\Exception $e) {
            $this->stop();
            throw $e;
        }
    }

    public function output()
    {
        return (string)file_get_contents($this->logFile);
    }

    public function stop()
    {
        proc_terminate($this->process);
        proc_close($this->process);
        // Fails, with a warning, if php still has the file open.
        unlink($this->logFile);
    }

    // Waits until the port takes connections, and fails as soon as php exits, for example because
    // another process took the port first.
    private function waitUntilListening($port)
    {
        $deadline = microtime(true) + 10;
        while (!($socket = @fsockopen('127.0.0.1', $port))) {
            $this->assertRunning();
            if (microtime(true) > $deadline) {
                throw new \RuntimeException("php -S did not start:\n" . $this->output());
            }
            usleep(50000);
        }
        fclose($socket);
        // If another process had the port, the connection reached it, and php exits right away.
        usleep(100000);
        $this->assertRunning();
    }

    private function assertRunning()
    {
        if (!proc_get_status($this->process)['running']) {
            throw new \RuntimeException("php -S exited:\n" . $this->output());
        }
    }

    private static function freePort()
    {
        $server = stream_socket_server('tcp://127.0.0.1:0');
        $name = stream_socket_get_name($server, false);
        fclose($server);
        return (int)substr(strrchr($name, ':'), 1);
    }

    // proc_open takes an array only from PHP 7.4 on; "exec" makes proc_terminate stop php itself,
    // not the shell, on Linux and macOS (bypass_shell does it on Windows).
    private static function commandLine(array $command)
    {
        $line = implode(' ', array_map('escapeshellarg', $command));
        return DIRECTORY_SEPARATOR === '\\' ? $line : "exec $line";
    }

    private static function nullDevice()
    {
        return DIRECTORY_SEPARATOR === '\\' ? 'NUL' : '/dev/null';
    }
}
