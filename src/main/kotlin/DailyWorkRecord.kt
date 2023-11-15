data class DailyWorkRecord(
    val payrollConfig: PayrollConfig,
    var inTime: String = payrollConfig.defInTime,
    var outTime: String = payrollConfig.defOutTime,
    var dayType: String = payrollConfig.defDayType,
    var regOvertimeHrs: Int = 0,
    var nsHrs: Int = 0,
    var nsOvertimeHrs: Int = 0,
    var salary: Float = 0.0F,
    var isNightShift: Boolean = false,
    var isAbsent: Boolean
) {
    init {
        val nInTime = inTime.toInt()
        var nOutTime = outTime.toInt()
        isAbsent = nInTime == nOutTime
        val workHours = payrollConfig.maxHours * 100 + 100

        //Check if "OUT" time is after midnight (e.g., "0000")
        if (nOutTime < nInTime) {
            nOutTime += 2400
        }
        //Check if the day is night shift
        isNightShift = nOutTime >= 2200
        var isOvertime: Boolean = nOutTime > (nInTime + workHours)

        if (!isNightShift && !isOvertime) {
            nsHrs = 0
            nsOvertimeHrs = 0
            regOvertimeHrs = 0
        }
        if (!isNightShift && isOvertime) {
            nsHrs = 0
            nsOvertimeHrs = 0
            regOvertimeHrs = (nOutTime - (nInTime + workHours)) / 100
        }
        if (isNightShift && !isOvertime) {
            regOvertimeHrs = 0
            nsOvertimeHrs = 0
            nsHrs = (nOutTime - 2200) / 100
        }
        if (isNightShift && isOvertime) {
            regOvertimeHrs = (2200 - (nInTime + workHours)) / 100
            nsHrs = 0 //fix thissssssssssssssssssssssssssssss
            nsOvertimeHrs = (nOutTime - 2200) / 100
        }
    }
}
