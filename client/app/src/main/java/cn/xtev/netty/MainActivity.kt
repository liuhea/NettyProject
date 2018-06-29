package cn.xtev.netty

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

/**
 * @author liuhe
 * @date 2018-06-29
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txt_main_conn.setOnClickListener { connectServer() }
    }

    private fun connectServer() {


    }
}
