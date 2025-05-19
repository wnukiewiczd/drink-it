package com.example.drinkit

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.Animator
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.BounceInterpolator
import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import android.view.View
import android.widget.ImageView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.animation.doOnEnd

@Composable
fun SplashScreen(onAnimationFinished: () -> Unit) {
    var animationStarted by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Stan dla głównej animacji logo
    var logoVisible by remember { mutableStateOf(false) }
    val logoScale by animateFloatAsState(
        targetValue = if (logoVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = ""
    )

    // Stan dla innych elementów
    var bubblesVisible by remember { mutableStateOf(false) }
    var textVisible by remember { mutableStateOf(false) }
    
    // Opóźnienia dla poszczególnych animacji
    LaunchedEffect(Unit) {
        animationStarted = true
        delay(100)
        logoVisible = true
        delay(600)
        bubblesVisible = true
        delay(1000)
        textVisible = true
        delay(2000)
        onAnimationFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        // Główne logo aplikacji
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.drinkit_logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(200.dp)
                    .scale(logoScale)
                    .clip(CircleShape)
                    .background(
                        color = MaterialTheme.colorScheme.background,
                        shape = CircleShape
                    ),
                contentScale = ContentScale.Fit
            )
        }
        
        // Animowane bąbelki za pomocą ObjectAnimator
        if (bubblesVisible) {
            AndroidView(
                factory = { context ->
                    // Tworzymy kontener dla naszych animowanych bąbelków
                    val container = android.widget.FrameLayout(context)
                    container.layoutParams = android.widget.FrameLayout.LayoutParams(
                        android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                        android.widget.FrameLayout.LayoutParams.MATCH_PARENT
                    )

                    // Tworzymy i dodajemy 5 bąbelków o różnych rozmiarach i kolorach
                    val colors = listOf(
                        android.graphics.Color.parseColor("#9C27B0"), // fioletowy
                        android.graphics.Color.parseColor("#2196F3"), // niebieski
                        android.graphics.Color.parseColor("#4CAF50"), // zielony
                        android.graphics.Color.parseColor("#FFC107"), // żółty
                        android.graphics.Color.parseColor("#FF5722")  // pomarańczowy
                    )
                    
                    val animatorSet = AnimatorSet()
                    val animations = mutableListOf<Animator>() // Zmiana typu listy z ObjectAnimator na Animator
                    
                    for (i in 0 until 5) {
                        val bubble = ImageView(context)
                        bubble.setImageResource(R.drawable.drinkit_logo)
                        bubble.scaleType = ImageView.ScaleType.FIT_CENTER
                        
                        val size = (30 + (i * 10)).dp.value.toInt()
                        val params = android.widget.FrameLayout.LayoutParams(size, size)
                        
                        // Ustawiamy początkową pozycję bąbelka
                        val screenWidth = context.resources.displayMetrics.widthPixels
                        val screenHeight = context.resources.displayMetrics.heightPixels
                        val startX = (0.4f + Math.random() * 0.2f) * screenWidth
                        val startY = screenHeight.toFloat()
                        
                        params.leftMargin = startX.toInt() - (size / 2)
                        params.topMargin = startY.toInt()
                        
                        bubble.layoutParams = params
                        container.addView(bubble)
                        
                        // Tworzymy animatory
                        val translateY = ObjectAnimator.ofFloat(
                            bubble, View.TRANSLATION_Y, 0f, -screenHeight * 0.5f
                        ).apply {
                            duration = (2000 + (i * 500)).toLong()
                            interpolator = AccelerateDecelerateInterpolator()
                        }
                        
                        val translateX = ObjectAnimator.ofFloat(
                            bubble, View.TRANSLATION_X, 0f, (Math.random() * 100 - 50).toFloat()
                        ).apply {
                            duration = (2000 + (i * 500)).toLong()
                            interpolator = BounceInterpolator()
                        }
                        
                        val rotation = ObjectAnimator.ofFloat(
                            bubble, View.ROTATION, 0f, (Math.random() * 360).toFloat()
                        ).apply {
                            duration = (2000 + (i * 300)).toLong()
                        }
                        
                        val scale = ObjectAnimator.ofFloat(
                            bubble, View.SCALE_X, 0.2f, 1f
                        ).apply {
                            duration = (1000 + (i * 200)).toLong()
                            interpolator = OvershootInterpolator()
                        }
                        
                        val scaleY = ObjectAnimator.ofFloat(
                            bubble, View.SCALE_Y, 0.2f, 1f
                        ).apply {
                            duration = (1000 + (i * 200)).toLong()
                            interpolator = OvershootInterpolator()
                        }
                        
                        val alpha = ObjectAnimator.ofFloat(
                            bubble, View.ALPHA, 0.4f, 1f, 0f
                        ).apply {
                            duration = (2000 + (i * 500)).toLong()
                        }
                        
                        // Dodajemy animatory do listy typu Animator
                        animations.add(translateY)
                        animations.add(translateX)
                        animations.add(rotation)
                        animations.add(scale)
                        animations.add(scaleY)
                        animations.add(alpha)
                    }
                    
                    // Uruchamiamy wszystkie animacje równocześnie
                    animatorSet.playTogether(animations)
                    animatorSet.start()
                    
                    container
                },
                modifier = Modifier.fillMaxSize()
            )
        }
        
        // Animowany tekst
        if (textVisible) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 64.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val titleAlpha by animateFloatAsState(
                    targetValue = if (textVisible) 1f else 0f,
                    animationSpec = tween(durationMillis = 800),
                    label = ""
                )
                val subtitleAlpha by animateFloatAsState(
                    targetValue = if (textVisible) 1f else 0f,
                    animationSpec = tween(durationMillis = 800, delayMillis = 300),
                    label = ""
                )
                
                Text(
                    text = "DrinkIt",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .graphicsLayer { alpha = titleAlpha }
                        .padding(bottom = 8.dp)
                )
                
                Text(
                    text = "Discover the coctail world",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.graphicsLayer { alpha = subtitleAlpha }
                )
            }
        }
    }
}
