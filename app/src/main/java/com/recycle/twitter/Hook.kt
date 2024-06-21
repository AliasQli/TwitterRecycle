package com.recycle.twitter

import com.highcapable.yukihookapi.hook.param.PackageParam

abstract class Hook {
    abstract fun PackageParam.load()
    fun init(packageParam: PackageParam) = packageParam.load()
}