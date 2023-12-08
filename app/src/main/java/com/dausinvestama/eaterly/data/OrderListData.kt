package com.dausinvestama.eaterly.data

data class OrderListData(
    val canteenId: Any?,
    val canteenName: Any?,
    val menus: List<Menu>
)

data class Menu(
    val menuId: Any?,
    val menuName: Any?,
    val menuQuantity: Any?,
    val menuStatus: Any?,
    val menuTotalprice: Any?
)
