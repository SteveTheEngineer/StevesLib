package me.ste.library.menu

interface MenuDataProvider {
    val containerId: Int
    val data: List<MenuDataEntry>
}