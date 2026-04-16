import com.android.build.gradle.AppExtension

val android = project.extensions.getByType(AppExtension::class.java)

android.apply {
    flavorDimensions("environment")

    productFlavors {
        create("stage") {
            dimension = "environment"
            applicationId = "com.timeclock.attendance.stage"
            versionNameSuffix = "-stage"
            resValue(type = "string", name = "app_name", value = "打卡(Stage)")
        }
        create("dev") {
            dimension = "environment"
            applicationId = "com.timeclock.attendance.dev"
            versionNameSuffix = "-dev"
            resValue(type = "string", name = "app_name", value = "打卡(Dev)")
        }
        create("pro") {
            dimension = "environment"
            applicationId = "com.timeclock.attendance"
            resValue(type = "string", name = "app_name", value = "打卡")
        }
    }
}