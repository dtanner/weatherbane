package weathervane.util

import groovy.util.logging.Slf4j

@Slf4j
class CommandUtil {

    /**
     * Simplist way to execute an operating system command, with an optional execution directory and timeout.
     * CAVEAT: This is limited in that it won't handle multi-value arguments.  e.g. "foo.sh -args 'bar baz'" won't be handled correctly.
     * @param command the command to run
     * @param executionDir a File directory, or the current directory if not provided
     * @param timeoutMillis - timeout in milliseconds to wait for command to finish, or 5 minutes if not provided
     * @return true if the process returned with a zero exit code.  false if it returned a non-zero exit code or timeout out
     */
    static boolean execute(String command, File executionDir = new File('.'), int timeoutMillis = 30000) {
        log.info "Executing ${command}"
        int start = System.currentTimeMillis()
        StringBuffer sout = new StringBuffer()
        StringBuffer serr = new StringBuffer()
        Process process = command.execute(null, executionDir)
        process.consumeProcessOutput(sout, serr)
        process.waitForOrKill(timeoutMillis)
        if (sout) log.info "stdout=${sout}"
        if (serr) log.warn "stderr=${serr}"
        int elapsed = System.currentTimeMillis() - start

        if (elapsed >= timeoutMillis) {
            log.error "The script took longer than ${timeoutMillis / 1000} seconds to run so it was killed"
            return false
        }

        return process.exitValue() == 0
    }

    /**
     * Way to execute an OS command when more parameter control is needed.  Properly handles multi-valued arguments and spacing.
     * @param processBuilder
     * @param timeoutMillis
     * @return true if the process returned with a zero exit code within the given timeout
     */
    static boolean execute(ProcessBuilder processBuilder, int timeoutMillis = 30000) {
        int start = System.currentTimeMillis()
        StringBuffer sout = new StringBuffer()
        StringBuffer serr = new StringBuffer()

        Process process = processBuilder.start()
        process.consumeProcessOutput(sout, serr)
        process.waitForOrKill(timeoutMillis)

        if (sout) log.info "stdout=${sout}"
        if (serr) log.warn "stderr=${serr}"

        int elapsed = System.currentTimeMillis() - start
        if (elapsed >= timeoutMillis) {
            log.error "The script took longer than ${timeoutMillis / 1000} seconds to run so it was killed"
            return false
        }

        return process.exitValue() == 0
    }

}

