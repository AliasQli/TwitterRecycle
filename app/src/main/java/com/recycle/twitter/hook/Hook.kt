package com.recycle.twitter.hook

import com.highcapable.yukihookapi.hook.param.PackageParam

abstract class Hook {
    abstract fun PackageParam.load()
    fun init(packageParam: PackageParam) = packageParam.load()
}