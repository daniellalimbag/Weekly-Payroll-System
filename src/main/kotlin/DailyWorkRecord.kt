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
