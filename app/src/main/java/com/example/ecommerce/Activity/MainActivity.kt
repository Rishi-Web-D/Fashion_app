package com.example.ecommerce.Activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat.startActivity
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ecommerce.Model.CategoryModel
import com.example.ecommerce.Model.ItemsModel
import com.example.ecommerce.Model.SliderModel
import com.example.ecommerce.R
import com.example.ecommerce.ViewModel.MainViewModel

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
                MainActivityScreen{
                    startActivity(Intent(this,CartActivity::class.java))
                }
        }
    }
}

@Composable
fun MainActivityScreen(onCartClick:()->Unit){
    val viewModel=MainViewModel()


    val banners= remember { mutableStateListOf<SliderModel>() }
    val categories = remember { mutableStateListOf<CategoryModel>() }
    val popular = remember { mutableStateListOf<ItemsModel>() }


    var showBannerLoading by remember { mutableStateOf(true) }
    var showCategoryLoading by remember { mutableStateOf(true) }
    var showPopularLoading by remember { mutableStateOf(true) }



    //Banner
    LaunchedEffect(Unit) {
        viewModel.loadBanner().observeForever {
            banners.clear()
            banners.addAll(it)
            showBannerLoading=false
        }
    }

    //Category
    LaunchedEffect (Unit) {
        viewModel.loadCategory().observeForever {
            categories.clear()
            categories.addAll(it)
            showCategoryLoading=false
        }
    }

    //Popular
    LaunchedEffect(Unit) {
        viewModel.loadPopular().observeForever {
            popular.clear()
            popular.addAll(it)
            showPopularLoading=false
        }
    }


    ConstraintLayout (modifier = Modifier.background(Color.White)) {
        val(scrollList,bottomMenu)=createRefs()
        LazyColumn (
            modifier = Modifier
                .fillMaxSize()
                .constrainAs(scrollList) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                    start.linkTo(parent.start)
                }
        ) {
            item {
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 70.dp)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Welcome Back" , color = Color.Black)
                        Text("Rishi",
                            color = Color.Black,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                            )
                    }
                    Row {
                        Image(
                            painter = painterResource(R.drawable.search_icon),
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Image(
                            painter = painterResource(R.drawable.bell_icon),
                            contentDescription = null
                        )
                    }
                }
            }
            //Banners will be here
            item {
                if(showBannerLoading){
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ){
                        CircularProgressIndicator()
                    }
                }else{
                    Banners(banners)
                }
            }

            //Category Heading
            item {
                Text(
                    text = "Official Brand",
                    color = Color.Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp)
                        .padding(horizontal = 16.dp)
                )
            }
            //Category Data
            item {
                if(showCategoryLoading){
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        contentAlignment = Alignment.Center
                    ){
                        CircularProgressIndicator()
                    }
                }else{
                    CategoryList(categories)
                }
            }

            //Popular Heading
            item {
                SectionTitle("Most Popular","See All")
            }
            //Popular Items
            item {
                if(showPopularLoading){
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                        contentAlignment = Alignment.Center
                    ){
                        CircularProgressIndicator()
                    }
                }else{
                    ListItems(items = popular)
                }
            }
        }
            //Bottom Menu
            BottomMenu(
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(bottomMenu){
                        bottom.linkTo(parent.bottom)

                    },
                onItemClick = onCartClick
            )

    }
}

@Composable
fun CategoryList(categories: SnapshotStateList<CategoryModel>) {
    var selectedIndex by remember { mutableStateOf(-1) }
    val context = LocalContext.current

    LazyRow (
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp,top = 8.dp)
    ) {
        items (categories.size){ index ->
            CategoryItem(item = categories[index], isSelected = selectedIndex==index){
                selectedIndex = index
                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(context,ListItemsActivity::class.java).apply {
                        putExtra("id",categories[index].id.toString())
                        putExtra("title",categories[index].title)
                    }
                    startActivity(context,intent,null)
                },500)
            }
        }
    }
}

@Composable
fun CategoryItem(item:CategoryModel,isSelected:Boolean,onItemClick:()->Unit){
    Column (
        modifier = Modifier
            .clickable (onClick = onItemClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = (item.picUrl),
            contentDescription = item.title,
            modifier = Modifier
                .size(if(isSelected)60.dp else 50.dp)
                .background(
                    color = if(isSelected) colorResource(R.color.darkBrown) else colorResource(R.color.lightBrown),
                    shape = RoundedCornerShape(100.dp)
                ),
            contentScale = ContentScale.Inside,
            colorFilter = if(isSelected){
                ColorFilter.tint(Color.White)
            }else{
                ColorFilter.tint(Color.Black)
            }
        )
        Spacer(modifier = Modifier.padding(top = 8.dp))
        Text(
            text = item.title,
            color = colorResource(R.color.darkBrown),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SectionTitle(title: String, actionText: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top=24.dp)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Text(
            text = title,
            color = Color.Black,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = actionText,
            color = colorResource(R.color.darkBrown)
        )
    }
}

@Composable
fun Banners(banners: List<SliderModel>) {
    AutoSlidingCarousel(banners=banners)
}

@Composable
fun AutoSlidingCarousel(
    modifier: Modifier = Modifier.padding(top=16.dp),
    pagerState : PagerState = rememberPagerState(initialPage = 1){3},
    banners: List<SliderModel>
) {
    val isDragged by pagerState.interactionSource.collectIsDraggedAsState()
    Column (
        modifier=Modifier.fillMaxSize(),
    ) {
        HorizontalPager(state = pagerState) {
            page ->
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(banners[page].url)
                            .error(R.drawable.banner0)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(top = 16.dp, bottom = 8.dp)
                            .height(150.dp)
                    )
           }
        DotIndicator(modifier= Modifier
            .padding(horizontal = 8.dp)
            .align(Alignment.CenterHorizontally),
            totalDots = banners.size,
            selectedIndex = if(isDragged)pagerState.currentPage else pagerState.currentPage,
            dotSize = 8.dp
        )
    }
}


@Composable
fun DotIndicator(
    modifier: Modifier=Modifier,
    totalDots : Int,
    selectedIndex: Int,
    selectedColor : Color= colorResource(R.color.darkBrown),
    unselectedColor : Color= colorResource(R.color.grey),
    dotSize : Dp
){
    LazyRow (modifier = modifier.wrapContentSize()) {
        items(totalDots){index ->
            IndicatorDot(
                color = if(index==selectedIndex)selectedColor else unselectedColor,
                size = dotSize,

            )
            if(index!= totalDots-1){
                Spacer(modifier=Modifier.padding(horizontal = 2.dp))
            }
        }
    }
}

@Composable
fun IndicatorDot(
    modifier: Modifier=Modifier,
    size: Dp,
    color:Color
){
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(color)
    )
}


@Composable
fun BottomMenu(modifier: Modifier=Modifier,onItemClick: () -> Unit){
    Row (
        modifier = modifier
            .padding(start = 16.dp, end = 16.dp, bottom = 32.dp)
            .background(
                colorResource(R.color.darkBrown),
                RoundedCornerShape(10.dp)
                ),
        horizontalArrangement = Arrangement.SpaceAround

    ) {
        BottomMenuItem(icon = painterResource(R.drawable.btn_1), text = "Explorer")
        BottomMenuItem(icon = painterResource(R.drawable.btn_2), text = "Cart",onItemClick=onItemClick)
        BottomMenuItem(icon = painterResource(R.drawable.btn_3), text = "Favorites")
        BottomMenuItem(icon = painterResource(R.drawable.btn_4), text = "Orders")
        BottomMenuItem(icon = painterResource(R.drawable.btn_5), text = "Profile")
    }
}

@Composable
fun BottomMenuItem(icon:Painter,text:String,onItemClick:(()->Unit)?=null){
    Column (
        modifier = Modifier
            .height(70.dp)
            .clickable { onItemClick?.invoke() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(icon,contentDescription = text, tint = Color.White)
        Spacer(Modifier.padding(vertical = 4.dp))
        Text(text, color = Color.White, fontSize = 10.sp)
    }
}