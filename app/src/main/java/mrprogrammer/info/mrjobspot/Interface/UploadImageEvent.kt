package mrprogrammer.info.mrjobspot.Interface



interface UploadImageEvent {
    fun onUploadCompleted(downloadUrl : String,key: String)
    fun onUploadFailed(cause:String)
    fun onUploadProgress(level:Double)
}