package com.fehty.instacopy.Activity.Realm

import io.realm.RealmObject

open class TokenRealm : RealmObject() {
    var userToken: String? = null
}