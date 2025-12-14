package com.hook.automation

import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.xposed.IYukiHookXposedInit
import com.highcapable.yukihookapi.hook.factory.encase

@InjectYukiHookWithXposed
object HookEntry : IYukiHookXposedInit {
    override fun onInit() = configs {
        isDebug = BuildConfig.DEBUG
        debugTag = "HookAutomation"
    }

    override fun onHook() = encase {
        DynamicHookManager.loadHooks(this)
    }
}