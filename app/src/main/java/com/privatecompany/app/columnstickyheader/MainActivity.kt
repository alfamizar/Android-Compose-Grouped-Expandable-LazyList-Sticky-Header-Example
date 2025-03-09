@file:OptIn(ExperimentalFoundationApi::class)

package com.privatecompany.app.columnstickyheader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                TransactionScreen()
            }
        }
    }
}

data class Transaction(
    val id: Int,
    val description: String,
    val amount: Double,
    val date: String,
    val category: String
)

data class DateGroup(
    val date: String,
    val transactions: List<Transaction>
)

@Composable
fun TransactionScreen() {
    val transactions = listOf(
        Transaction(1, "Starbucks", 5.99, "2025-02-27", "Coffee Shop"),
        Transaction(2, "Whole Foods", 45.30, "2025-02-27", "Grocery"),
        Transaction(3, "Barnes & Noble", 23.75, "2025-02-27", "Book Store"),
        Transaction(4, "CVS Pharmacy", 15.20, "2025-02-27", "Pharmacy"),
        Transaction(5, "Starbucks", 4.50, "2025-02-27", "Coffee Shop"),
        Transaction(6, "McDonalds", 8.99, "2025-02-26", "Fast Food"),
        Transaction(7, "Shell Gas", 40.00, "2025-02-26", "Gas Station"),
        Transaction(8, "AMC Theater", 18.00, "2025-02-26", "Entertainment"),
        Transaction(9, "Walgreens", 12.30, "2025-02-26", "Pharmacy"),
        Transaction(10, "App Store", 9.99, "2025-02-26", "Digital"),
        Transaction(11, "Kroger", 67.89, "2025-02-25", "Grocery"),
        Transaction(12, "H&M", 54.20, "2025-02-25", "Clothing"),
        Transaction(13, "Supercuts", 35.00, "2025-02-25", "Personal Care"),
        Transaction(14, "PetSmart", 28.45, "2025-02-25", "Pet Store"),
        Transaction(15, "McDonalds", 7.85, "2025-02-25", "Fast Food"),
        Transaction(16, "Netflix", 12.99, "2025-02-24", "Digital"),
        Transaction(17, "Home Depot", 38.75, "2025-02-24", "Hardware"),
        Transaction(18, "Car Wash", 15.00, "2025-02-24", "Car Care"),
        Transaction(19, "Burger King", 9.85, "2025-02-24", "Fast Food"),
        Transaction(20, "Starbucks", 6.25, "2025-02-24", "Coffee Shop"),
        Transaction(21, "Cinema City", 25.00, "2025-02-26", "Entertainment"),
        Transaction(22, "PS Store", 438.99, "2025-07-29", "Hardware"),
        )

    val groupedByCategory = transactions.groupBy { it.category }
        .mapValues { entry ->
            entry.value.groupBy { it.date }
                .map { DateGroup(it.key, it.value) }
                .sortedBy { it.date }
        }
        .toSortedMap()

    TransactionList(groupedByCategory)
}

@Composable
fun TransactionList(groupedByCategory: Map<String, List<DateGroup>>) {
    val expandedCategories = remember {
        mutableStateMapOf<String, Boolean>().apply {
            groupedByCategory.keys.forEach { put(it, true) }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
        ,
        contentPadding = PaddingValues(16.dp)
    ) {
        groupedByCategory.forEach { (category, dateGroups) ->
            item {
                CategoryHeader(
                    category = category,
                    isExpanded = expandedCategories[category] ?: true,
                    onExpandClick = {
                        expandedCategories[category] = !(expandedCategories[category] ?: true)
                    }
                )
            }

            if (expandedCategories[category] == true) {
                dateGroups.forEach { group ->
                    stickyHeader {
                        DateHeader(date = group.date)
                    }

                    items(group.transactions) { transaction ->
                        AnimatedVisibility(
                            visible = expandedCategories[category] ?: true,
                            enter = expandVertically(
                                animationSpec = tween(durationMillis = 300)
                            ) + fadeIn(
                                animationSpec = tween(durationMillis = 300)
                            ),
                            exit = shrinkVertically(
                                animationSpec = tween(durationMillis = 300)
                            ) + fadeOut(
                                animationSpec = tween(durationMillis = 300)
                            )
                        ) {
                            TransactionItem(transaction = transaction)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DateHeader(date: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(vertical = 8.dp, horizontal = 16.dp)
    ) {
        Text(
            text = date,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
fun CategoryHeader(category: String, isExpanded: Boolean, onExpandClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onExpandClick)
            .padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = category,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Expand/Collapse",
                modifier = Modifier.rotate(if (isExpanded) 0f else 180f)
            )
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = transaction.description,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = transaction.date,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            Text(
                text = "$${String.format("%.2f", transaction.amount)}",
                color = if (transaction.amount < 0) Color.Green else Color.Red
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TransactionScreenPreview() {
    MaterialTheme {
        TransactionScreen()
    }
}