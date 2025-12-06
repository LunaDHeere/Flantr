package com.example.flantr.ui.home

import androidx.lifecycle.ViewModel
import com.example.flantr.data.model.Route
import com.example.flantr.data.model.Stop
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class HomeUiState (
    //TODO: add logic to save a route to the profile of a user (should be done with a list maybe?)
    // and maybe an addList function
    val savedRoutes: List<Route> = emptyList(),
    //TODO: add logic for "popular routes" -> i have no fucking clue how i'm gonna do that bro
    val popularRoutes: List<Route> = emptyList()
)

class HomeViewModel: ViewModel(){
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init{
        loadMockData()
    }

    //using mock data right now because i don't want to traumatise myself with
    //a database rn. Also the mockdata is AI generated because THIS is where AI actually
    //speeds up the process of writing code.
    private fun loadMockData(){
        val mockStops = listOf(
            Stop("1", "The Corner Bookshop", "123 Main St", "Cozy reading nooks", 45),
            Stop("2", "Brew & Pages", "456 Oak Ave", "Great latte", 30)
        )
        val saved = listOf(
            Route("s1", "Weekend Downtown Walk", "Custom", "My favorite spots", 150, "2.1 miles", mockStops),
            Route("s2", "Hidden Gems", "Exploration", "Off the beaten path", 180, "3.2 miles", mockStops)
        )
        val popular = listOf(
            Route(
                id = "1",
                name = "Coffee & Books Morning",
                theme = "Bookstore & Coffee",
                description = "A cozy morning visiting independent bookstores",
                totalTimeMinutes = 180,
                distance = "2.3 miles",
                stops = mockStops,
                imageUrl = "https://images.unsplash.com/photo-1587566657649-2b1a1a5c79e4?w=1080"
            ),
            Route(
                id = "2",
                name = "Urban Art Trail",
                theme = "Art & Culture",
                description = "Explore stunning street art and galleries",
                totalTimeMinutes = 240,
                distance = "3.5 miles",
                stops = mockStops,
                imageUrl = "https://images.unsplash.com/photo-158030306457-e54f25fe4384?w=1080"
            ),
            Route(
                id = "3",
                name = "Foodie Crawl",
                theme = "Food & Drinks",
                description = "Sample the best local flavors",
                totalTimeMinutes = 210,
                distance = "1.8 miles",
                stops = mockStops,
                imageUrl = "https://images.unsplash.com/photo-1529686398651-b8112f4bb98c?w=1080"
            )
        )
        _uiState.update { currentState ->
            currentState.copy(
                savedRoutes = saved,
                popularRoutes = popular
            )
        }
    }
}