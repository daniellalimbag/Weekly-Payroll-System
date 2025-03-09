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
)
