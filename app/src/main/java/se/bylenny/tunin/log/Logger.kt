package se.bylenny.tunin.log

import okhttp3.Interceptor
import kotlin.reflect.KClass
import kotlin.reflect.full.companionObject

/**
 * @see [https://stackoverflow.com/questions/34416869/idiomatic-way-of-logging-in-kotlin/34462577#34462577]
 */

interface Logger {

    fun debug(message: String)
    fun error(throwable: Throwable)
    fun info(message: String)
    fun warn(message: String)
}

interface LoggerFactory {
    fun create(name: String): Logger

    companion object {
        lateinit var instance: LoggerFactory
    }
}

// Return logger for Java class, if companion object fix the name
fun <T: Any> logger(forClass: Class<T>): Logger {
    return LoggerFactory.instance.create(
        unwrapCompanionClass(
            forClass
        ).name
    )
}

// unwrap companion class to enclosing class given a Java Class
fun <T : Any> unwrapCompanionClass(ofClass: Class<T>): Class<*> {
    return ofClass.enclosingClass?.takeIf {
        ofClass.enclosingClass?.kotlin?.companionObject?.java == ofClass
    } ?: ofClass
}

// unwrap companion class to enclosing class given a Kotlin Class
fun <T: Any> unwrapCompanionClass(ofClass: KClass<T>): KClass<*> {
    return unwrapCompanionClass(ofClass.java).kotlin
}

// Return logger for Kotlin class
fun <T: Any> logger(forClass: KClass<T>): Logger {
    return logger(forClass.java)
}

// return logger from extended class (or the enclosing class)
fun <T: Any> T.logger(): Logger {
    return logger(this.javaClass)
}

// return a lazy logger property delegate for enclosing class
fun <R : Any> R.lazyLogger(): Lazy<Logger> {
    return lazy { logger(this.javaClass) }
}

// return a logger property delegate for enclosing class
fun <R : Any> R.injectLogger(): Lazy<Logger> {
    return lazyOf(logger(this.javaClass))
}

// marker interface and related extension (remove extension for Any.logger() in favour of this)
interface Loggable {}
fun Loggable.logger(): Logger =
    logger(this.javaClass)

// abstract base class to provide logging, intended for companion objects more than classes but works for either
abstract class WithLogging: Loggable {
    val LOG = logger()
}

// Return logging interceptor for Java class
fun <T : Any> loggingInterceptor(forClass: Class<T>): Interceptor =
    LoggingInterceptor(logger(forClass))