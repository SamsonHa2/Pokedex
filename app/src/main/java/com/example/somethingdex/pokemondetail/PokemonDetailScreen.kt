package com.example.somethingdex.pokemondetail

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.somethingdex.data.models.PokedexListEntry
import com.example.somethingdex.util.Resource
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun PokemonDetailScreen(
    id: String,
    backColor: Color = Color.White,
    navController: NavController,
    topPadding: Dp = 20.dp,
    pokemonImageSize: Dp = 200.dp,
    viewModel: PokemonDetailViewModel = hiltViewModel()
) {
    val pokemonInfo = produceState<Resource<PokedexListEntry>>(initialValue = Resource.Loading()) {
        value = viewModel.getPokemonInfo(id)
    }.value
    Box(modifier = Modifier
        .fillMaxSize()
        .background(backColor)
        .padding(bottom = 16.dp)
    ) {
        PokemonDetailTopSection(
            navController = navController,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.2f)
                .align(Alignment.TopCenter)
        )
        PokemonDetailStateWrapper(
            pokemonInfo = pokemonInfo,
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = topPadding + pokemonImageSize / 2f,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                )
                .shadow(10.dp, RoundedCornerShape(10.dp))
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(.16.dp)
                .align(Alignment.BottomCenter),
            loadingModifier = Modifier
                .size(100.dp)
                .align(Alignment.Center)
                .padding(
                    top = topPadding + pokemonImageSize / 2f,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                )
            )
        Box(contentAlignment = Alignment.TopCenter,
            modifier = Modifier
            .fillMaxSize()) {

            if (pokemonInfo is Resource.Success){
                pokemonInfo.data?.let {
                    AsyncImage(
                        model = it.imageUrl,
                        contentDescription = it.pokemonName,
                        modifier = Modifier
                            .size(pokemonImageSize)
                            .offset(y = topPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun PokemonDetailTopSection(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.TopStart,
        modifier = modifier
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.Black,
                        Color.Transparent
                    )
                )
            )
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .size(36.dp)
                .offset(16.dp, 16.dp)
                .clickable {
                    navController.popBackStack()
                }
        )
    }
}

@Composable
fun PokemonDetailStateWrapper(
    pokemonInfo: Resource<PokedexListEntry>,
    modifier: Modifier = Modifier,
    loadingModifier: Modifier = Modifier
) {
    when (pokemonInfo) {
        is Resource.Success -> {
            PokemonDetailSection(
                pokemonInfo = pokemonInfo.data!!,
                modifier = modifier
                    .offset(y = (-20).dp)
            )
        }
        is Resource.Error -> {
            Text(
                text = pokemonInfo.message!!,
                color = Color.Red,
                modifier = modifier
                )
        }
        is Resource.Loading -> {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = loadingModifier
            )
        }
    }
}

@Composable
fun PokemonDetailSection(
    pokemonInfo: PokedexListEntry,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .offset(y = 100.dp)
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 22.dp)
        ) {
            Text(
                text = "#${pokemonInfo.number.toString().padStart(4, '0')}",
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = pokemonInfo.pokemonName,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                textAlign = TextAlign.End,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .weight(1f)
            )
        }
        PokemonTypeSection(types = pokemonInfo.types)
        DescriptionSection(description = pokemonInfo.description)
        PokemonDetailDataSection(
            pokemonWeight = pokemonInfo.weight,
            pokemonHeight = pokemonInfo.height
        )
        PokemonBaseStats(pokemonInfo = pokemonInfo)
    }
}

@Composable
fun DescriptionSection(description: String){
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 20.dp, vertical = 16.dp)) {
        Text(
            text = description,
            fontSize = 12.sp,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White, RoundedCornerShape(10.dp))
                .padding(horizontal = 10.dp, vertical = 10.dp)
        )
    }
}

@Composable
fun PokemonTypeSection(types: List<String>) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        Spacer(modifier = Modifier.width(0.dp))
        for (type in types) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, Color.White, RoundedCornerShape(20.dp))
                    .background(Color.Transparent)
                    .height(35.dp)
            ) {
                Text(
                    text = type.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(
                            Locale.ROOT
                        ) else it.toString()
                    },
                    color = Color.White,
                    fontSize = 18.sp
                )
            }
        }
        Spacer(modifier = Modifier.width(0.dp))
    }
}

@Composable
fun PokemonDetailDataSection(
    pokemonWeight: Double,
    pokemonHeight: Double,
) {
    val pokemonWeightInKg = remember {
        (pokemonWeight * 100f).roundToInt() / 1000f
    }
    val pokemonHeightInMeters = remember {
        (pokemonHeight * 100f).roundToInt() / 1000f
    }
    Row(
        horizontalArrangement = Arrangement.spacedBy(22.dp),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.width(0.dp))
        PokemonDetailDataItem(
            dataValue = pokemonWeightInKg,
            dataUnit = "kg",
            modifier = Modifier.weight(1f)
        )
        PokemonDetailDataItem(
            dataValue = pokemonHeightInMeters,
            dataUnit = "m",
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(0.dp))
    }
}

@Composable
fun PokemonDetailDataItem(
    dataValue: Float,
    dataUnit: String,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White, RoundedCornerShape(10.dp))
            .border(1.dp, Color.LightGray, RoundedCornerShape(10.dp))
    ){
        Text(
            text = "$dataValue" + dataUnit,
            fontSize = 14.sp,
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White, RoundedCornerShape(10.dp))
                .padding(horizontal = 22.dp, vertical = 18.dp)
        )
    }
}


@Composable
fun PokemonStat(
    statName: String,
    statValue: Int,
    statMaxValue: Int,
    height: Dp = 28.dp,
    animDuration: Int = 1000,
    animDelay: Int = 0
) {
    var animationPlayed by remember {
        mutableStateOf(false)
    }
    val curPercent = animateFloatAsState(
        targetValue = if(animationPlayed) {
            statValue / statMaxValue.toFloat()
        } else 0f,
        animationSpec = tween(
            animDuration,
            animDelay
        ), label = ""
    )
    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }
    Box(
        modifier = Modifier
            .height(height)
            .clip(CircleShape)
            .background(
                if (isSystemInDarkTheme()) {
                    Color(0xFF505050)
                } else {
                    Color.LightGray
                }
            )
            .fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(curPercent.value)
                .clip(CircleShape)
                .background(Color.White)
                .padding(horizontal = 8.dp)
        ) {
            Text(
                text = statName,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = (curPercent.value * statMaxValue).toInt().toString(),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun PokemonBaseStats(
    pokemonInfo: PokedexListEntry,
    animDelayPerItem: Int = 100
) {
    val statNames = listOf("HP", "Atk", "Def", "SpAtk", "SpAtk", "Spd")
    val stats = listOf(pokemonInfo.hp, pokemonInfo.attack, pokemonInfo.defense, pokemonInfo.specialAttack, pokemonInfo.specialDefense, pokemonInfo.speed)
    val maxBaseStat = remember {
        maxOf(pokemonInfo.hp, pokemonInfo.attack, pokemonInfo.defense, pokemonInfo.specialAttack, pokemonInfo.specialDefense, pokemonInfo.speed)
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        for(i in stats.indices) {
            val stat = stats[i]
            PokemonStat(
                statName = statNames[i],
                statValue = stat,
                statMaxValue = maxBaseStat,
                animDelay = i * animDelayPerItem
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}