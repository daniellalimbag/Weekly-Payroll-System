data class DailyWorkRecord(
        val payrollConfig: PayrollConfig,
        var inTime: String = payrollConfig.defInTime,
        var outTime: String = payrollConfig.defOutTime,
        var dayType: String = payrollConfig.defDayType,
        var regOvertimeHrs: Float = 0.0F,
        var nsHrs: Float = 0.0F,
        var nsOvertimeHrs: Float = 0.0F,
        var salary: Float = 0.0F,
        var isNightShift: Boolean = false,
        var isAbsent: Boolean,
        var isRestDay: Boolean
)  {
    init {
        val nInTime = inTime.toInt()
        var nOutTime = outTime.toInt()

        isAbsent = nInTime == nOutTime
        val workHours = payrollConfig.maxHours * 100 + 100

        if (isRestDay) {
            when (dayType) {
                "Normal" -> dayType = "Rest Day"
                "Regular Holiday" -> dayType = "Regular Holiday and Rest Day"
                "Special Non-Working Day" -> dayType = "Special Non-Working Day and Rest Day"
            }
        }

        if (nOutTime < nInTime) {
            nOutTime += 2400
        }

        isNightShift = nOutTime >= 2200
        var isOvertime: Boolean = nOutTime > (nInTime + workHours)

        if (!isNightShift && !isOvertime) {
            nsHrs = 0.0F
            nsOvertimeHrs = 0.0F
            regOvertimeHrs = 0.0F
        }
        if (!isNightShift && isOvertime) {
            nsHrs = 0.0F
            nsOvertimeHrs = 0.0F
            val minutes = subtractMilitaryTimes(nOutTime, (nInTime + workHours))
            regOvertimeHrs = minutes / 60.0F
        }
        if (isNightShift && !isOvertime) {
            regOvertimeHrs = 0.0F
            nsOvertimeHrs = 0.0F
            nsHrs = subtractMilitaryTimes(nOutTime, 2200) / 60.0F
        }
        if (isNightShift && isOvertime) {
            val rMinutes = subtractMilitaryTimes(2200, (nInTime + workHours))
            regOvertimeHrs = rMinutes / 60.0F
            nsHrs = 0.0F // fix thissssssssssssssssssssssssssssss
            nsOvertimeHrs = subtractMilitaryTimes(nOutTime, 2200) / 60.0F
        }

        println("NS hours: $nsHrs")
        println("OT hours: $regOvertimeHrs")
        println("NSOT hours: $nsOvertimeHrs")
    }

    fun subtractMilitaryTimes(time1: Int, time2: Int): Int {
        val hours1 = time1 / 100
        val minutes1 = time1 % 100

        val hours2 = time2 / 100
        val minutes2 = time2 % 100

        val totalMinutes1 = hours1 * 60 + minutes1
        val totalMinutes2 = hours2 * 60 + minutes2

        return totalMinutes1 - totalMinutes2
    }
}
