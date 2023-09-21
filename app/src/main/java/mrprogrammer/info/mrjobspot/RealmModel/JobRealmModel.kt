package mrprogrammer.info.mrjobspot.RealmModel

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class JobRealmModel :RealmObject() {
    @PrimaryKey
    var id:String = ""
    var name:String = ""
    var title:String = ""
    var about:String = ""
    var from:String = ""
    var mobile:String = ""
    var skill:String = ""
    var image:String = ""
    var imagekey:String = ""
    var address:String = ""
    var locality:String = ""
    var date: String = ""
    var time: String = ""
    var lat: String = ""
    var lon: String = ""
}