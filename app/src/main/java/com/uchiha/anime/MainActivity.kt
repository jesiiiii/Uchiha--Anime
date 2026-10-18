package com.uchiha.anime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Ink = Color(0xFF101014)
private val Panel = Color(0xFF1B1B22)
private val Red = Color(0xFFE53935)
private val Muted = Color(0xFF9A9AA5)
private val White = Color(0xFFF7F7FA)

data class Anime(val title: String, val subtitle: String, val genre: String, val emoji: String, val score: String)
private val animeList = listOf(
    Anime("Naruto Shippuden", "The road to becoming Hokage", "ACTION", "🍥", "9.2"),
    Anime("Attack on Titan", "Humanity's last stand", "DARK FANTASY", "⚔️", "9.1"),
    Anime("Jujutsu Kaisen", "Curses are born from fear", "SUPERNATURAL", "👁️", "8.8"),
    Anime("Demon Slayer", "A blade against the night", "ADVENTURE", "🔥", "8.7"),
    Anime("One Piece", "The grand adventure begins", "ADVENTURE", "☠️", "9.0"),
    Anime("Solo Leveling", "Arise, hunter", "FANTASY", "🌑", "8.9")
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { UchihaApp() }
    }
}

@Composable
fun UchihaApp() {
    var page by remember { mutableStateOf("Home") }
    var selected by remember { mutableStateOf(animeList[0]) }
    var search by remember { mutableStateOf("") }
    var favorites by remember { mutableStateOf(setOf<String>()) }
    MaterialTheme(colorScheme = darkColorScheme(
        primary = Red, onPrimary = White, background = Ink, surface = Panel,
        onBackground = White, onSurface = White, secondary = Color(0xFFFF766F)
    )) {
        Scaffold(
            containerColor = Ink,
            bottomBar = {
                NavigationBar(containerColor = Color(0xFF17171D), contentColor = White) {
                    listOf(
                        Triple("Home", Icons.Filled.Home, "Home"),
                        Triple("Explore", Icons.Filled.Explore, "Categories"),
                        Triple("Saved", Icons.Filled.Favorite, "Favorites"),
                        Triple("Profile", Icons.Filled.Person, "Profile")
                    ).forEach { item ->
                        NavigationBarItem(
                            selected = page == item.third,
                            onClick = { page = item.third },
                            icon = { Icon(item.second, contentDescription = item.first) },
                            label = { Text(item.first, fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Red, selectedTextColor = Red,
                                indicatorColor = Red.copy(alpha = .14f),
                                unselectedIconColor = Muted, unselectedTextColor = Muted
                            )
                        )
                    }
                }
            }
        ) { padding ->
            Box(Modifier.fillMaxSize().padding(padding)) {
                when (page) {
                    "Home" -> HomeScreen(search, { search = it }, favorites,
                        onOpen = { selected = it; page = "Details" },
                        onAll = { page = "Categories" })
                    "Categories" -> CategoriesScreen(onOpen = { selected = it; page = "Details" })
                    "Favorites" -> FavoritesScreen(favorites, onOpen = { selected = it; page = "Details" })
                    "Profile" -> ProfileScreen()
                    "Details" -> DetailsScreen(selected, favorites.contains(selected.title),
                        onBack = { page = "Home" },
                        onFavorite = { isFav ->
                            favorites = if (isFav) favorites + selected.title else favorites - selected.title
                        })
                    else -> HomeScreen(search, { search = it }, favorites,
                        onOpen = { selected = it; page = "Details" }, onAll = { page = "Categories" })
                }
            }
        }
    }
}

@Composable
private fun ScreenHeader(eyebrow: String, title: String, trailing: String? = null, onTrailing: (() -> Unit)? = null) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Column {
            Text(eyebrow.uppercase(), color = Red, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            Text(title, color = White, fontSize = 27.sp, fontWeight = FontWeight.ExtraBold)
        }
        if (trailing != null) Text(trailing, color = Red, fontSize = 13.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.clip(RoundedCornerShape(10.dp)).clickable { onTrailing?.invoke() }.padding(8.dp))
    }
}

@Composable
private fun HomeScreen(search: String, onSearch: (String) -> Unit, favorites: Set<String>,
                       onOpen: (Anime) -> Unit, onAll: () -> Unit) {
    val filtered = animeList.filter { it.title.contains(search, true) }
    LazyColumn(contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(42.dp).clip(RoundedCornerShape(14.dp)).background(Red), contentAlignment = Alignment.Center) {
                    Text("う", color = White, fontSize = 26.sp, fontWeight = FontWeight.Black)
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text("UCHIHA", color = White, fontWeight = FontWeight.Black, letterSpacing = 3.sp, fontSize = 18.sp)
                    Text("ANIME UNIVERSE", color = Muted, fontSize = 9.sp, letterSpacing = 2.sp)
                }
                Icon(Icons.Filled.NotificationsNone, null, tint = White, modifier = Modifier.size(26.dp))
            }
        }
        item {
            OutlinedTextField(value = search, onValueChange = onSearch, modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search anime, genre...", color = Muted) },
                leadingIcon = { Icon(Icons.Filled.Search, null, tint = Red) },
                trailingIcon = { if (search.isNotEmpty()) IconButton(onClick = { onSearch("") }) { Icon(Icons.Filled.Close, null) } },
                singleLine = true, shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Red, unfocusedBorderColor = Color(0xFF34343D),
                    focusedTextColor = White, unfocusedTextColor = White,
                    cursorColor = Red, focusedContainerColor = Panel, unfocusedContainerColor = Panel
                ))
        }
        item {
            Box(Modifier.fillMaxWidth().height(190.dp).clip(RoundedCornerShape(24.dp))
                .background(Brush.linearGradient(listOf(Color(0xFF711B25), Color(0xFF28151D), Color(0xFF17171D))))
                .clickable { onOpen(animeList[0]) }.padding(20.dp)) {
                Column(Modifier.align(Alignment.CenterStart).fillMaxWidth(.72f), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                    Text("FEATURED • #01", color = Color(0xFFFFA09A), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
                    Text("THE SHINOBI
LEGACY", color = White, fontSize = 25.sp, lineHeight = 28.sp, fontWeight = FontWeight.Black)
                    Text("Enter a world of legends.", color = Color(0xFFE5C9CC), fontSize = 12.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.PlayArrow, null, tint = White, modifier = Modifier.size(17.dp))
                        Text(" Explore now", color = White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Text("忍", modifier = Modifier.align(Alignment.CenterEnd), color = Color.White.copy(alpha = .10f),
                    fontSize = 94.sp, fontWeight = FontWeight.Black)
            }
        }
        item { SectionTitle("Continue Watching", "SEE ALL", onAll) }
        item {
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                animeList.take(3).forEachIndexed { index, anime ->
                    Column(Modifier.width(220.dp).clip(RoundedCornerShape(16.dp)).background(Panel).clickable { onOpen(anime) }) {
                        AnimePoster(anime, Modifier.fillMaxWidth().height(105.dp))
                        Column(Modifier.padding(12.dp)) {
                            Text(anime.title, color = White, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text("Episode ${index + 8}  •  24 min", color = Muted, fontSize = 11.sp)
                            Spacer(Modifier.height(8.dp))
                            Box(Modifier.fillMaxWidth().height(3.dp).clip(CircleShape).background(Color(0xFF3A3034))) {
                                Box(Modifier.fillMaxWidth(if (index == 0) .72f else .38f).fillMaxHeight().background(Red))
                            }
                        }
                    }
                }
            }
        }
        item { SectionTitle(if (search.isBlank()) "Trending Anime" else "Search Results", "VIEW ALL", onAll) }
        items(filtered) { anime ->
            AnimeRow(anime, favorites.contains(anime.title), onOpen)
        }
        if (filtered.isEmpty()) item { Text("No anime found. Try another title.", color = Muted, modifier = Modifier.padding(16.dp)) }
    }
}

@Composable
private fun SectionTitle(title: String, action: String, onAction: () -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(title, color = White, fontSize = 19.sp, fontWeight = FontWeight.Bold)
        Text(action, color = Red, fontSize = 10.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { onAction() }.padding(4.dp))
    }
}

@Composable
private fun AnimePoster(anime: Anime, modifier: Modifier = Modifier) {
    Box(modifier.clip(RoundedCornerShape(14.dp)).background(
        Brush.linearGradient(listOf(Color(0xFF57232C), Color(0xFF25222D), Color(0xFF121218)))),
        contentAlignment = Alignment.Center) {
        Text(anime.emoji, fontSize = 48.sp)
        Text(anime.genre, color = White.copy(alpha = .85f), fontSize = 9.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.BottomStart).padding(10.dp))
    }
}

@Composable
private fun AnimeRow(anime: Anime, isFavorite: Boolean, onOpen: (Anime) -> Unit) {
    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Panel).clickable { onOpen(anime) }.padding(10.dp),
        verticalAlignment = Alignment.CenterVertically) {
        AnimePoster(anime, Modifier.size(width = 88.dp, height = 100.dp))
        Column(Modifier.weight(1f).padding(horizontal = 12.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(anime.title, color = White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(anime.subtitle, color = Muted, fontSize = 11.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text("★ ${anime.score}   •   ${anime.genre}", color = Color(0xFFFFC56D), fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
        Icon(if (isFavorite) Icons.Filled.Favorite else Icons.Filled.ChevronRight, null, tint = if (isFavorite) Red else Muted)
    }
}

@Composable
private fun CategoriesScreen(onOpen: (Anime) -> Unit) {
    val categories = listOf("All", "Action", "Adventure", "Fantasy", "Drama", "Supernatural", "Comedy")
    var chosen by remember { mutableStateOf("All") }
    Column(Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        Spacer(Modifier.height(22.dp))
        ScreenHeader("Find your next story", "Explore")
        Spacer(Modifier.height(16.dp))
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            categories.forEach { category ->
                FilterChip(selected = chosen == category, onClick = { chosen = category }, label = { Text(category) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Red, selectedLabelColor = White))
            }
        }
        Spacer(Modifier.height(16.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 20.dp)) {
            items(animeList.filter { chosen == "All" || it.genre.contains(chosen.uppercase()) }) { AnimeRow(it, false, onOpen) }
        }
    }
}

@Composable
private fun FavoritesScreen(favorites: Set<String>, onOpen: (Anime) -> Unit) {
    val saved = animeList.filter { favorites.contains(it.title) }
    Column(Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        Spacer(Modifier.height(22.dp))
        ScreenHeader("Your personal collection", "Favorites")
        Spacer(Modifier.height(18.dp))
        if (saved.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(Icons.Filled.FavoriteBorder, null, tint = Red, modifier = Modifier.size(54.dp))
                    Text("Nothing saved yet", color = White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Open an anime and tap the heart to save it.", color = Muted, fontSize = 12.sp)
                }
            }
        } else LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) { items(saved) { AnimeRow(it, true, onOpen) } }
    }
}

@Composable
private fun DetailsScreen(anime: Anime, isFavorite: Boolean, onBack: () -> Unit, onFavorite: (Boolean) -> Unit) {
    LazyColumn(contentPadding = PaddingValues(bottom = 28.dp)) {
        item {
            Box(Modifier.fillMaxWidth().height(300.dp).background(
                Brush.linearGradient(listOf(Color(0xFF7C1D2B), Color(0xFF28151D), Ink)))) {
                Text("忍", color = White.copy(alpha = .12f), fontSize = 150.sp, fontWeight = FontWeight.Black,
                    modifier = Modifier.align(Alignment.CenterEnd).padding(end = 20.dp))
                IconButton(onClick = onBack, modifier = Modifier.align(Alignment.TopStart).padding(12.dp).background(Ink.copy(alpha = .7f), CircleShape)) {
                    Icon(Icons.Filled.ArrowBack, null, tint = White)
                }
                AnimePoster(anime, Modifier.align(Alignment.BottomStart).padding(start = 22.dp, bottom = 0.dp).size(width = 120.dp, height = 150.dp))
            }
        }
        item {
            Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(anime.genre, color = Red, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                Text(anime.title, color = White, fontSize = 28.sp, fontWeight = FontWeight.Black)
                Text("★ ${anime.score}    •    TV Series    •    24 min / ep", color = Color(0xFFFFC56D), fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(onClick = { }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Red),
                        shape = RoundedCornerShape(14.dp)) {
                        Icon(Icons.Filled.PlayArrow, null); Spacer(Modifier.width(5.dp)); Text("Preview UI")
                    }
                    OutlinedButton(onClick = { onFavorite(!isFavorite) }, shape = RoundedCornerShape(14.dp)) {
                        Icon(if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder, null,
                            tint = if (isFavorite) Red else White)
                    }
                }
                Text("Synopsis", color = White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("${anime.subtitle}. Follow the journey through a world of unforgettable characters, intense rivalries, and stories that test the limits of courage. This is placeholder text for UI testing.", color = Muted, fontSize = 14.sp, lineHeight = 22.sp)
                Text("Episodes", color = White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                repeat(4) { index ->
                    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Panel).padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(56.dp, 42.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFF49202A)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Filled.PlayCircle, null, tint = White)
                        }
                        Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                            Text("Episode ${index + 1}", color = White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("A new chapter begins", color = Muted, fontSize = 11.sp)
                        }
                        Text("24 min", color = Muted, fontSize = 10.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileScreen() {
    Column(Modifier.fillMaxSize().padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(24.dp))
        ScreenHeader("Your anime space", "Profile")
        Spacer(Modifier.height(36.dp))
        Box(Modifier.size(104.dp).clip(CircleShape).background(Brush.linearGradient(listOf(Red, Color(0xFF571B2C)))),
            contentAlignment = Alignment.Center) {
            Icon(Icons.Filled.Person, null, tint = White, modifier = Modifier.size(60.dp))
        }
        Spacer(Modifier.height(14.dp))
        Text("Shinobi Guest", color = White, fontSize = 22.sp, fontWeight = FontWeight.Black)
        Text("Demo profile • Local preview", color = Muted, fontSize = 12.sp)
        Spacer(Modifier.height(28.dp))
        listOf(
            Icons.Filled.History to "Watch history",
            Icons.Filled.Download to "Downloads",
            Icons.Filled.NotificationsNone to "Notifications",
            Icons.Filled.Settings to "App settings",
            Icons.Filled.Info to "About Uchiha Anime"
        ).forEach { (icon, label) ->
            Row(Modifier.fillMaxWidth().padding(vertical = 5.dp).clip(RoundedCornerShape(14.dp)).background(Panel).clickable { }.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = Red)
                Text(label, color = White, modifier = Modifier.weight(1f).padding(start = 14.dp), fontWeight = FontWeight.Medium)
                Icon(Icons.Filled.ChevronRight, null, tint = Muted)
            }
        }
        Spacer(Modifier.weight(1f))
        Text("UCHIHA ANIME  •  UI DEMO 0.1.0", color = Muted, fontSize = 9.sp, letterSpacing = 1.5.sp)
    }
}
