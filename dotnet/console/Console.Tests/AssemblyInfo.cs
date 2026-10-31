using Xunit.Sdk;
using Xunit.v3;

// The tests set environment variables (SIGNER_ENDPOINT, SIGNER_API_KEY), the current directory and
// System.Console's output, which the whole process shares, so they run one at a time.
[assembly: Parallelization(Mode = ParallelMode.None)]
