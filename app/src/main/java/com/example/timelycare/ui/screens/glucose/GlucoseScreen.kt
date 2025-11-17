package com.example.timelycare.ui.screens.glucose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.timelycare.data.GlucoseRepository
import com.example.timelycare.ui.theme.*
import java.time.LocalDate

@Composable
fun GlucoseScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val repository = remember { GlucoseRepository.getInstance() }
    val todayData by repository.todayData.collectAsStateWithLifecycle()
    val historicalReadings by repository.historicalReadings.collectAsStateWithLifecycle()
    val dateNavItems = remember(todayData.date, historicalReadings) {
        val items = historicalReadings.map {
            DateNavItem(it.date, it.displayDate)
        }.toMutableList()
        if (items.none { it.date == todayData.date }) {
            items.add(DateNavItem(todayData.date, "Today"))
        }
        items
    }
    var selectedNavIndex by remember(dateNavItems) { mutableIntStateOf(0) }
    LaunchedEffect(dateNavItems.size) {
        if (dateNavItems.isNotEmpty()) {
            selectedNavIndex = selectedNavIndex.coerceIn(0, dateNavItems.lastIndex)
        }
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(TimelyCareBlue)
                .padding(top = 4.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = TimelyCareWhite
                )
            }

            Text(
                text = "Glucose Report",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TimelyCareWhite,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Date Navigation
            DateNavigationCard(
                items = dateNavItems,
                selectedIndex = selectedNavIndex,
                onPrevious = {
                    if (selectedNavIndex < dateNavItems.lastIndex) {
                        selectedNavIndex++
                    }
                },
                onNext = {
                    if (selectedNavIndex > 0) {
                        selectedNavIndex--
                    }
                }
            )

            // Current Glucose Reading Card
            CurrentGlucoseReadingCard(dailyData = todayData)

            // Daily Glucose Chart
            DailyGlucoseChart(dailyData = todayData)

            // Past Week History
            PastWeekGlucoseHistory(historicalReadings = historicalReadings)
        }
    }
}

private data class DateNavItem(val date: LocalDate, val label: String)

@Composable
private fun DateNavigationCard(
    items: List<DateNavItem>,
    selectedIndex: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hasItems = items.isNotEmpty()
    val currentLabel = if (hasItems) items[selectedIndex].label else "Today"
    val positionLabel = if (hasItems) "${selectedIndex + 1} of ${items.size}" else "0 of 0"
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = TimelyCareWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onPrevious,
                enabled = hasItems && selectedIndex < items.lastIndex
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "Previous day",
                    tint = TimelyCareTextSecondary
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Calendar",
                    tint = TimelyCareBlue,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = currentLabel,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TimelyCareTextPrimary
                )
            }

            IconButton(
                onClick = onNext,
                enabled = hasItems && selectedIndex > 0
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Next day",
                    tint = TimelyCareTextSecondary
                )
            }
        }

        // Pagination dots
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, _ ->
                val isActive = index == selectedIndex
                Box(
                    modifier = Modifier
                        .size(if (isActive) 8.dp else 6.dp)
                        .padding(horizontal = 2.dp)
                ) {
                    androidx.compose.foundation.Canvas(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        drawCircle(
                            color = if (isActive) Color(0xFF4299E1) else Color(0xFFE2E8F0),
                            radius = size.minDimension / 2
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = positionLabel,
                fontSize = 12.sp,
                color = TimelyCareTextSecondary
            )
        }
    }
}