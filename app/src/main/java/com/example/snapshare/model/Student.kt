package com.example.snapshare.model

import android.os.Parcel
import android.os.Parcelable

data class Student(
    var name: String,
    var id: String,
    var imageUrl: String,
    var phone: String,
    var address: String,
    var cb: Boolean
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(id)
        parcel.writeString(imageUrl)
        parcel.writeString(phone)
        parcel.writeString(address)
        parcel.writeByte(if (cb) 1 else 0)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Student> {
        override fun createFromParcel(parcel: Parcel): Student = Student(parcel)
        override fun newArray(size: Int): Array<Student?> = arrayOfNulls(size)
    }
}