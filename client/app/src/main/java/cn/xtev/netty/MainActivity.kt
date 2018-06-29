package cn.xtev.netty

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*


/**
 * https://blog.gmem.cc/netty-study-note
 * https://blog.csdn.net/yulinxx/article/details/51085782
 * @author liuhe
 * @date 2018-06-29
 */
class MainActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.java.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_main_conn.setOnClickListener { connectServer() }
    }

    private fun connectServer() {
        val client = NettyClientThread()
        client?.start()
    }
}
