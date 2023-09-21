package mrprogrammer.info.mrjobspot.Utils


import android.os.Handler

class Debouncer(private val delayMillis: Long) {
    private val handler = Handler()
    private var runnable: Runnable? = null

    fun debounce(action: () -> Unit) {
        runnable?.let { handler.removeCallbacks(it) }

        val newRunnable = Runnable {
            action.invoke()
        }

        runnable = newRunnable

        handler.postDelayed(newRunnable, delayMillis)
    }
}
