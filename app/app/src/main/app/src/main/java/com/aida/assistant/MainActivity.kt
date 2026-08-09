package com.aida.assistant

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.view.MotionEvent
import android.view.View
import java.util.Locale
import kotlin.math.sin
import kotlin.random.Random

class MainActivity : Activity(), TextToSpeech.OnInitListener {

    private lateinit var aidaView: AidaView
    private lateinit var speech: SpeechRecognizer
    private lateinit var tts: TextToSpeech

    private var ouvindo = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.rgb(8, 2, 18)
        window.navigationBarColor = Color.rgb(3, 1, 8)

        aidaView = AidaView()
        setContentView(aidaView)

        tts = TextToSpeech(this, this)

        prepararReconhecimento()

        if (
            checkSelfPermission(
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.RECORD_AUDIO),
                100
            )
        }
    }

    override fun onInit(status: Int) {

        if (status == TextToSpeech.SUCCESS) {

            tts.language = Locale("pt", "BR")

            tts.setSpeechRate(0.88f)
            tts.setPitch(1.15f)
        }
    }

    private fun prepararReconhecimento() {

        speech =
            SpeechRecognizer.createSpeechRecognizer(this)

        speech.setRecognitionListener(
            object : RecognitionListener {

                override fun onReadyForSpeech(
                    params: Bundle?
                ) {

                    ouvindo = true

                    aidaView.status =
                        "Estou ouvindo..."

                    aidaView.invalidate()
                }

                override fun onBeginningOfSpeech() {
                    ouvindo = true
                    aidaView.invalidate()
                }

                override fun onRmsChanged(
                    rmsdB: Float
                ) {

                    aidaView.audio = rmsdB
                    aidaView.invalidate()
                }

                override fun onBufferReceived(
                    buffer: ByteArray?
                ) {}

                override fun onEndOfSpeech() {

                    ouvindo = false
                    aidaView.invalidate()
                }

                override fun onError(
                    error: Int
                ) {

                    ouvindo = false

                    aidaView.status =
                        "Não consegui entender."

                    aidaView.invalidate()
                }

                override fun onResults(
                    results: Bundle?
                ) {

                    ouvindo = false

                    val resultados =
                        results?.getStringArrayList(
                            SpeechRecognizer.RESULTS_RECOGNITION
                        )

                    val comando =
                        resultados
                            ?.firstOrNull()
                            ?.lowercase(
                                Locale("pt", "BR")
                            )

                    if (!comando.isNullOrBlank()) {
                        executar(comando)
                    }

                    aidaView.invalidate()
                }

                override fun onPartialResults(
                    partialResults: Bundle?
                ) {}

                override fun onEvent(
                    eventType: Int,
                    params: Bundle?
                ) {}
            }
        )
    }

    private fun ouvir() {

        if (
            checkSelfPermission(
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            requestPermissions(
                arrayOf(
                    Manifest.permission.RECORD_AUDIO
                ),
                100
            )

            return
        }

        if (ouvindo) {

            speech.stopListening()
            ouvindo = false

            return
        }

        val intent =
            Intent(
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH
            )

        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )

        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            "pt-BR"
        )

        intent.putExtra(
            RecognizerIntent.EXTRA_MAX_RESULTS,
            1
        )

        speech.startListening(intent)

        aidaView.status =
            "Preparando..."

        aidaView.invalidate()
    }

    private fun executar(
        comando: String
    ) {

        aidaView.ultimoComando = comando

        when {

            comando.contains("oi") ||
            comando.contains("olá") ||
            comando.contains("ola") -> {

                falar(
                    "Olá. Eu sou AIDA. Como posso ajudar?"
                )
            }

            comando.contains("quem é você") ||
            comando.contains("quem e voce") -> {

                falar(
                    "Eu sou AIDA, sua assistente virtual."
                )
            }

            comando.contains("hora") ||
            comando.contains("horas") -> {

                val hora =
                    java.text.SimpleDateFormat(
                        "HH:mm",
                        Locale("pt", "BR")
                    ).format(
                        java.util.Date()
                    )

                falar(
                    "Agora são $hora."
                )
            }

            comando.contains("youtube") -> {

                falar("Abrindo o YouTube.")

                abrir(
                    "com.google.android.youtube",
                    "https://youtube.com"
                )
            }

            comando.contains("whatsapp") -> {

                falar("Abrindo o WhatsApp.")

                abrir(
                    "com.whatsapp",
                    "https://web.whatsapp.com"
                )
            }

            comando.contains("instagram") -> {

                falar("Abrindo o Instagram.")

                abrir(
                    "com.instagram.android",
                    "https://instagram.com"
                )
            }

            comando.contains("configurações") ||
            comando.contains("configuracoes") -> {

                falar("Abrindo as configurações.")

                startActivity(
                    Intent(Settings.ACTION_SETTINGS)
                )
            }

            comando.contains("wifi") ||
            comando.contains("wi-fi") -> {

                falar(
                    "Abrindo as configurações de Wi-Fi."
                )

                startActivity(
                    Intent(Settings.ACTION_WIFI_SETTINGS)
                )
            }

            comando.contains("bluetooth") -> {

                falar(
                    "Abrindo as configurações de Bluetooth."
                )

                startActivity(
                    Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
                )
            }

            comando.startsWith("pesquise") ||
            comando.startsWith("pesquisar") ||
            comando.startsWith("procure") -> {

                val pesquisa =
                    comando
                        .replaceFirst("pesquise", "")
                        .replaceFirst("pesquisar", "")
                        .replaceFirst("procure", "")
                        .trim()

                if (pesquisa.isNotEmpty()) {

                    falar(
                        "Pesquisando por $pesquisa."
                    )

                    val url =
                        "https://www.google.com/search?q=" +
                        Uri.encode(pesquisa)

                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(url)
                        )
                    )

                } else {

                    falar(
                        "O que você quer que eu pesquise?"
                    )
                }
            }

            comando.contains("ajuda") -> {

                falar(
                    "Você pode pedir para eu abrir aplicativos, abrir configurações, pesquisar ou dizer as horas."
                )
            }

            else -> {

                falar(
                    "Ainda não conheço esse comando."
                )
            }
        }
    }

    private fun abrir(
        pacote: String,
        fallback: String
    ) {

        try {

            val app =
                packageManager
                    .getLaunchIntentForPackage(
                        pacote
                    )

            if (app != null) {

                startActivity(app)

            } else {

                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(fallback)
                    )
                )
            }

        } catch (e: Exception) {

            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(fallback)
                )
            )
        }
    }

    private fun falar(
        mensagem: String
    ) {

        aidaView.status = mensagem
        aidaView.invalidate()

        tts.stop()

        tts.speak(
            mensagem,
            TextToSpeech.QUEUE_FLUSH,
            null,
            "AIDA"
        )
    }

    override fun onDestroy() {

        speech.destroy()

        tts.stop()
        tts.shutdown()

        super.onDestroy()
    }

    inner class AidaView :
        View(this@MainActivity) {

        private val paint =
            Paint(Paint.ANTI_ALIAS_FLAG)

        var status =
            "Toque no núcleo para falar"

        var ultimoComando =
            ""

        var audio =
            0f

        private var rotacao =
            0f

        private val particulas =
            ArrayList<Particula>()

        init {

            repeat(100) {

                particulas.add(
                    Particula(
                        Random.nextFloat(),
                        Random.nextFloat(),
                        Random.nextFloat() * 2f + 0.5f
                    )
                )
            }
        }

        override fun onDraw(
            canvas: Canvas
        ) {

            val w = width.toFloat()
            val h = height.toFloat()

            val cx = w / 2f
            val cy = h / 2f

            val fundo =
                RadialGradient(
                    cx,
                    cy,
                    h * .75f,
                    intArrayOf(
                        Color.rgb(45, 3, 70),
                        Color.rgb(12, 1, 25),
                        Color.rgb(2, 0, 6)
                    ),
                    null,
                    Shader.TileMode.CLAMP
                )

            paint.shader = fundo

            canvas.drawRect(
                0f,
                0f,
                w,
                h,
                paint
            )

            paint.shader = null

            paint.color =
                Color.rgb(
                    190,
                    70,
                    255
                )

            for (p in particulas) {

                p.y -= .0006f

                if (p.y < 0)
                    p.y = 1f

                canvas.drawCircle(
                    p.x * w,
                    p.y * h,
                    p.tamanho,
                    paint
                )
            }

            paint.textAlign =
                Paint.Align.CENTER

            paint.textSize =
                23f

            paint.typeface =
                Typeface.DEFAULT_BOLD

            paint.color =
                Color.rgb(
                    230,
                    170,
                    255
                )

            paint.setShadowLayer(
                18f,
                0f,
                0f,
                Color.MAGENTA
            )

            canvas.drawText(
                "A I D A",
                cx,
                70f,
                paint
            )

            paint.clearShadowLayer()

            paint.textSize = 10f

            paint.color =
                Color.rgb(
                    190,
                    100,
                    255
                )

            canvas.drawText(
                "● SISTEMA ONLINE",
                cx,
                95f,
                paint
            )

            val nucleoY =
                cy - 20f

            val raio =
                minOf(w, h) * .29f

            rotacao +=
                if (ouvindo)
                    2.5f
                else
                    .7f

            paint.style =
                Paint.Style.STROKE

            paint.strokeWidth = 2f

            paint.color =
                Color.rgb(
                    175,
                    30,
                    255
                )

            paint.setShadowLayer(
                20f,
                0f,
                0f,
                Color.MAGENTA
            )

            canvas.save()

            canvas.rotate(
                rotacao,
                cx,
                nucleoY
            )

            val oval =
                RectF(
                    cx - raio,
                    nucleoY - raio,
                    cx + raio,
                    nucleoY + raio
                )

            canvas.drawArc(
                oval,
                0f,
                300f,
                false,
                paint
            )

            canvas.restore()

            canvas.save()

            canvas.rotate(
                -rotacao * 1.5f,
                cx,
                nucleoY
            )

            paint.color =
                Color.rgb(
                    225,
                    70,
                    255
                )

            canvas.drawCircle(
                cx,
                nucleoY,
                raio * .77f,
                paint
            )

            canvas.restore()

            val pulso =
                1f +
                (
                    sin(
                        rotacao * .03
                    ) * .08
                ).toFloat()

            val r =
                raio * .25f * pulso

            paint.style =
                Paint.Style.FILL

            val brilho =
                RadialGradient(
                    cx,
                    nucleoY,
                    r * 2.5f,
                    intArrayOf(
                        Color.WHITE,
                        Color.rgb(
                            230,
                            130,
                            255
                        ),
                        Color.rgb(
                            150,
                            0,
                            255
                        ),
                        Color.TRANSPARENT
                    ),
                    null,
                    Shader.TileMode.CLAMP
                )

            paint.shader = brilho

            canvas.drawCircle(
                cx,
                nucleoY,
                r * 2.5f,
                paint
            )

            paint.shader = null

            val botaoY =
                h - 115f

            paint.color =
                Color.rgb(
                    35,
                    3,
                    50
                )

            canvas.drawCircle(
                cx,
                botaoY,
                38f,
                paint
            )

            paint.style =
                Paint.Style.STROKE

            paint.strokeWidth = 2f

            paint.color =
                Color.rgb(
                    190,
                    40,
                    255
                )

            canvas.drawCircle(
                cx,
                botaoY,
                38f,
                paint
            )

            paint.style =
                Paint.Style.FILL

            paint.textSize = 25f

            paint.color =
                if (ouvindo)
                    Color.RED
                else
                    Color.rgb(
                        225,
                        150,
                        255
                    )

            canvas.drawText(
                if (ouvindo) "●" else "🎙",
                cx,
                botaoY + 9f,
                paint
            )

            paint.textSize = 12f

            paint.color =
                Color.rgb(
                    205,
                    135,
                    255
                )

            canvas.drawText(
                status,
                cx,
                h - 45f,
                paint
            )

            paint.clearShadowLayer()

            invalidate()
        }

        override fun onTouchEvent(
            event: MotionEvent
        ): Boolean {

            if (
                event.action ==
                MotionEvent.ACTION_UP
            ) {

                ouvir()

                return true
            }

            return true
        }
    }

    data class Particula(
        var x: Float,
        var y: Float,
        var tamanho: Float
    )
}
