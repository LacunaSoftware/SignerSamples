<?php

namespace Lacuna\Tests;

/**
 * A php -S process for the tests, on a free port of localhost. What it writes to the console goes
 * to a file, which output() reads.
 */
class PhpServer
{
    private $process;
    private $logFile;
    public $url;

    // Starts "php -S localhost:<port> <router>" in the folder given, with these environment
    // variables added to the tests' own.
    public function __construct($folder, $router, array $environment)
    {
        $port = self::freePort();
        $this->url = "http://localhost:$port";
        $this->logFile = tempnam(sys_get_temp_dir(), 'php-server');
        $command = [PHP_BINARY, '-S', "localhost:$port", $router];
        $this->process = proc_open(
            self::commandLine($command),
            [0 => ['file', self::nullDevice(), 'r'], 1 => ['file', $this->logFile, 'a'], 2 => ['file', $this->logFile, 'a']],
            $pipes,
            $folder,
            array_merge(getenv(), $environment)
        );
        if (!is_resource($this->process)) {
            throw new \RuntimeException('Cannot start php -S');
        }
        $deadline = microtime(true) + 10;
        while (!($socket = @fsockopen('localhost', $port))) {
            if (microtime(true) > $deadline) {
                throw new \RuntimeException("php -S did not start:\n" . $this->output());
            }
            usleep(50000);
        }
        fclose($socket);
    }

    public function output()
    {
        return (string)file_get_contents($this->logFile);
    }

    public function stop()
    {
        proc_terminate($this->process);
        proc_close($this->process);
        @unlink($this->logFile);
    }

    private static function freePort()
    {
        $server = stream_socket_server('tcp://127.0.0.1:0');
        $name = stream_socket_get_name($server, false);
        fclose($server);
        return (int)substr(strrchr($name, ':'), 1);
    }

    // proc_open takes an array only from PHP 7.4 on; "exec" makes proc_terminate stop php itself,
    // not the shell, on Linux.
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
