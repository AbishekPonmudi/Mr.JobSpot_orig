package mrprogrammer.info.mrjobspot.RealmModel


import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class UserProfileInformation : RealmObject() {
    @PrimaryKey
    var id: String =""
    var email: String = ""
    var imageUrl: String = ""
    var mobile: String = ""
    var username: String = ""
    var about: String = ""
    var languages: String = ""
    var skills:String = ""
    var lat:String = ""
    var lon:String = ""
    var address:String = ""
    var locality:String = ""
    var work: RealmList<WorkRealm>? = null
}

open class WorkRealm : RealmObject() {
    @PrimaryKey
    var id: String = ""
    var dis: String = ""
    var end: String = ""
    var job: String = ""
    var office: String = ""
    var start: String = ""
}
