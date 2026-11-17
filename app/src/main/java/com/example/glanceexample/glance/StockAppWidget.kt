package com.example.glanceexample.glance

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Duration

class StockAppWidget : GlanceAppWidget() {

    private var job: Job? = null

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // Запускаем фоновое обновление только один раз
        if (job == null) {
            job = startUpdateJob(
                Duration.ofSeconds(20).toMillis(),
                context
            )
        }
        provideContent {
            GlanceTheme {
                GlanceContent()
            }
        }
    }

    private fun startUpdateJob(timeInterval: Long, context: Context): Job {
        return CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                PriceDataRepo.update()
                // Принудительно обновляем все экземпляры виджета
                StockAppWidget().updateAll(context)
                delay(timeInterval)
            }
        }
    }

    @Composable
    fun GlanceContent() {
        val stateCount by PriceDataRepo.currentPrice.collectAsState()
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.background)
                .padding(8.dp)
        ) {
            Text("GOOGL")
            Text("$stateCount")
            Text("${PriceDataRepo.change} %")
        }
    }
}