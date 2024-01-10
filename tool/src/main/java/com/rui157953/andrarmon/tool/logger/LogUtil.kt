package com.rui157953.andrarmon.tool.logger

import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.CsvFormatStrategy
import com.orhanobut.logger.DiskLogAdapter
import com.orhanobut.logger.DiskLogStrategy
import com.orhanobut.logger.FormatStrategy
import com.orhanobut.logger.LogStrategy
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import java.text.SimpleDateFormat
import java.util.Arrays
import java.util.Locale

object LogUtil {
    private const val MAX_BYTES = 500 * 1024
    const val GLOBAL_TAG = "SHT"
    const val VERBOSE = 2
    const val DEBUG = 3
    const val INFO = 4
    const val WARN = 5
    const val ERROR = 6
    const val ASSERT = 7
    var isDebug = false
    private var ht: HandlerThread? = null

    fun init() {
        val formatStrategy: FormatStrategy = PrettyFormatStrategy.newBuilder()
            .showThreadInfo(false) // (Optional) Whether to show thread info or not. Default true
            .methodCount(0) // (Optional) How many method line to show. Default 2
            .methodOffset(7) // (Optional) Hides internal method calls up to offset. Default 5
            //                .logStrategy(RT) // (Optional) Changes the log strategy to print out. Default LogCat
            .tag(GLOBAL_TAG) // (Optional) Global tag for every log. Default PRETTY_LOGGER
            .build()
        Logger.addLogAdapter(AndroidLogAdapter(formatStrategy))
    }

    fun setDebug(context: Context, debug: Boolean) {
        isDebug = debug
        if (debug) {
            val folder = context.applicationContext.getExternalFilesDir("log")!!
                .absolutePath
            ht = HandlerThread("AndroidFileLogger.$folder")
            ht!!.start()
            val handler: Handler = WriteHandler(
                ht!!.looper, folder, MAX_BYTES
            )
            val logStrategy: LogStrategy = DiskLogStrategy(handler)
            val csvFormatStrategy = CsvFormatStrategy.newBuilder()
                .tag(GLOBAL_TAG)
                .dateFormat(
                    SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss",
                        Locale.CHINA
                    )
                )
                .logStrategy(logStrategy)
                .build()
            Logger.addLogAdapter(DiskLogAdapter(csvFormatStrategy))
        } else {
            if (ht != null && ht!!.isAlive) {
                ht!!.quitSafely()
            }
            Logger.clearLogAdapters()
            init()
        }
    }

    fun t(tag: String?, message: String?) {
        log(Logger.DEBUG, tag, message, null)
    }

    fun log(priority: Int, tag: String?, message: String?, throwable: Throwable?) {
        var message = message
        val name = functionName
        if (name != null) {
            // Log.d(tag, name + " - " + str);
            message = "$name - $message"
        }
        Logger.log(priority, tag, message, throwable)
    }

    fun d(message: String, vararg args: Any?) {
        val msg = createMessage(message, *args)
        log(Logger.DEBUG, GLOBAL_TAG, msg, null)
    }

    fun d(`object`: Any?) {
        log(Logger.DEBUG, GLOBAL_TAG, toString(`object`), null)
    }

    fun e(message: String, vararg args: Any?) {
        val msg = createMessage(message, *args)
        log(Logger.ERROR, GLOBAL_TAG, msg, null)
    }

    fun e(throwable: Throwable?, message: String, vararg args: Any?) {
        log(Logger.ERROR, GLOBAL_TAG, message, throwable)
    }

    fun i(message: String, vararg args: Any?) {
        val msg = createMessage(message, *args)
        log(Logger.INFO, GLOBAL_TAG, msg, null)
    }

    fun v(message: String, vararg args: Any?) {
        val msg = createMessage(message, *args)
        log(Logger.VERBOSE, GLOBAL_TAG, msg, null)
    }

    fun w(message: String, vararg args: Any?) {
        val msg = createMessage(message, *args)
        log(Logger.WARN, GLOBAL_TAG, msg, null)
    }

    /**
     * Tip: Use this for exceptional situations to log
     * ie: Unexpected errors etc
     */
    fun wtf(message: String, vararg args: Any?) {
        val msg = createMessage(message, *args)
        log(Logger.ASSERT, GLOBAL_TAG, msg, null)
    }

    /**
     * Formats the given json content and print it
     */
    fun json(json: String?) {
        Logger.json(json)
    }

    /**
     * Formats the given xml content and print it
     */
    fun xml(xml: String?) {
        Logger.xml(xml)
    }

    private val functionName: String?
        private get() {
            val sts = Thread.currentThread().stackTrace
            for (st in sts) {
                if (st.isNativeMethod) {
                    continue
                }
                if (st.className == Thread::class.java.name) {
                    continue
                }
                if (st.className == LogUtil::class.java.name) {
                    continue
                }
                return st.fileName + ":" + st.lineNumber + "->"
            }
            return null
        }

    private fun createMessage(message: String, vararg args: Any?): String {
        return if (args == null || args.size == 0) message else String.format(message, *args)
    }

    fun toString(`object`: Any?): String {
        if (`object` == null) {
            return "null"
        }
        if (!`object`.javaClass.isArray) {
            return `object`.toString()
        }
        if (`object` is BooleanArray) {
            return Arrays.toString(`object` as BooleanArray?)
        }
        if (`object` is ByteArray) {
            return Arrays.toString(`object` as ByteArray?)
        }
        if (`object` is CharArray) {
            return Arrays.toString(`object` as CharArray?)
        }
        if (`object` is ShortArray) {
            return Arrays.toString(`object` as ShortArray?)
        }
        if (`object` is IntArray) {
            return Arrays.toString(`object` as IntArray?)
        }
        if (`object` is LongArray) {
            return Arrays.toString(`object` as LongArray?)
        }
        if (`object` is FloatArray) {
            return Arrays.toString(`object` as FloatArray?)
        }
        if (`object` is DoubleArray) {
            return Arrays.toString(`object` as DoubleArray?)
        }
        return if (`object` is Array<*>) {
            Arrays.deepToString(`object` as Array<Any?>?)
        } else "Couldn't find a correct type for the object"
    }
}