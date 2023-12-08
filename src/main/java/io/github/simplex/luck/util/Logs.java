package io.github.simplex.luck.util;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Logs
{
    private static final Logger logger = LoggerFactory.getLogger("FeelingLucky");

    private Logs()
    {
        throw new AssertionError();
    }

    public static void info(String message)
    {
        logger.info(message);
    }

    public static void warn(String message)
    {
        logger.warn(message);
    }

    public static void warn(String message, Throwable th)
    {
        logger.warn(message, th);
    }

    public static void warn(Throwable th)
    {
        final String msg = ExceptionUtils.getRootCauseMessage(th);
        logger.warn(msg);
    }

    public static void error(String message)
    {
        logger.error(message);
    }

    public static void error(String message, Throwable th)
    {
        logger.error(message, th);
    }

    public static void error(Throwable th)
    {
        final String msg = ExceptionUtils.getRootCauseMessage(th);
        logger.error(msg, th);
    }
}
