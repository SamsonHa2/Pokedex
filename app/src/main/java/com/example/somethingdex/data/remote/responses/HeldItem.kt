package com.example.somethingdex.data.remote.responses

data class HeldItem(
    val item: Item,
    val version_details: List<VersionDetail>
)