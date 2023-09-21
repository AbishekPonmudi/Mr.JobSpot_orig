package mrprogrammer.info.mrjobspot.FirebaseModel



data class UserInfo(
    val Email: String? = null,
    val Imageurl: String? = null,
    val Mobile: String? = null,
    val Username: String? = null,
    val about: String? = null,
    val lan: String? = null,
    val skill: String? = null,
    val lat: String? = null,
    val lon: String? = null,
    val address: String? = null,
    val locality: String? = null,
    val work: Map<String, Work>? = null)

data class Work(
    val dis: String? = null,
    val end: String? = null,
    val job: String? = null,
    val office: String? = null,
    val start: String? = null
)
