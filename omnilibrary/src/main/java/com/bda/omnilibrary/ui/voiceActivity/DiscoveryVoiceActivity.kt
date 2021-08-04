package com.bda.omnilibrary.ui.voiceActivity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bda.omnilibrary.R
import com.bda.omnilibrary.ui.baseActivity.BaseActivity


class DiscoveryVoiceActivity : BaseActivity() {

    private val REQUEST_VOICE_PERMISSIONS_REQUEST_CODE = 1
    private val REQUEST_VOICE_INPUT_ADDRESS_COPDE = 2

    private var REQUEST_CODE = -1

    companion object {
        val REQUEST_VOICE_ADDRESS_CODE = 3979
        val REQUEST_VOICE_PHONE_CODE = 123
        val REQUEST_VOICE_EMAIL_CODE = 456
        val REQUEST_VOICE_NAME_CODE = 789
        val REQUEST_VOICE_SEARCH_CODE = 8080
        var REQUEST_VOICE_RESULT = "result_code"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_discovery_voice)
        REQUEST_CODE = intent.getIntExtra(REQUEST_VOICE_RESULT, -1)
        if (REQUEST_CODE > -1) {
            voiceSpeechInput()
            setResult(REQUEST_CODE)
        } else {
            setResult(REQUEST_CODE)
            finish()
        }
    }

    private fun requestVoicePermission() {
        ActivityCompat.requestPermissions(
            this@DiscoveryVoiceActivity,
            arrayOf(
                Manifest.permission.RECORD_AUDIO
            ),
            REQUEST_VOICE_PERMISSIONS_REQUEST_CODE
        )
    }


    private fun voiceSpeechInput() {
        this.let {
            if (ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED
            ) {

                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                intent.putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM

                )
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, getDefaultLanguageCode())
                intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
                intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.text_chung_toi_muon_nghe)/*"Chúng tôi muốn nghe"*/)
                startActivityForResult(intent, REQUEST_VOICE_INPUT_ADDRESS_COPDE)
            } else {
                requestVoicePermission()
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_VOICE_PERMISSIONS_REQUEST_CODE) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                voiceSpeechInput()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            when (requestCode) {
                REQUEST_VOICE_INPUT_ADDRESS_COPDE -> {
                    showResult(result?.get(0).toString().trim())
                }

            }
        } else {
            finish()
        }
    }

    private fun showResult(data: String) {
        val intent = Intent()
        intent.putExtra(RecognizerIntent.EXTRA_RESULTS, data)
        setResult(REQUEST_CODE, intent)
        finish()

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
