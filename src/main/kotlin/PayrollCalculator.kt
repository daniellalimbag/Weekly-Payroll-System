import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class PayrollCalculator(
    var payrollConfig: PayrollConfig = PayrollConfig.Builder().build(),
    val weeklyWorkRecord: WeeklyWorkRecord
) {
    companion object {
        private val payRates = mapOf(
            "Normal" to 1.0f,
            "Rest Day" to 1.3f,
            "Special Non-Working Day" to 1.3f,
            "Special Non-Working Day and Rest Day" to 1.5f,
            "Regular Holiday" to 2.0f,
            "Regular Holiday and Rest Day" to 2.6f
        )

        private val overtimeRates = mapOf(
            "Normal" to 1.25f,
            "Rest Day" to 1.69f,
            "Special Non-Working Day" to 1.69f,
            "Special Non-Working Day and Rest Day" to 1.95f,
            "Regular Holiday" to 2.6f
        )

        private val nightShiftRates = mapOf(
            "Normal" to 1.375f,
            "Rest Day" to 1.859f,
            "Special Non-Working Day" to 1.859f,
            "Special Non-Working Day and Rest Day" to 2.145f,
            "Regular Holiday" to 2.86f
        )
    }

    /**
     * Validates if a given string is a valid military time format.
     */
    fun isValidMilitaryTime(time: String): Boolean {
        return try {
            LocalTime.parse(time, DateTimeFormatter.ofPattern("HHmm"))
            true
        } catch (e: DateTimeParseException) {
            false
        }
    }

    /**
     * Calculates the total weekly salary for all records in the weeklyWorkRecord.
     */
    fun calculateTotalSalary() {
        weeklyWorkRecord.totalSalary = weeklyWorkRecord.records.sumOf { record ->
            record.salary = calculateDailySalary(record)
            record.salary.toDouble()
        }.toFloat()
    }

    /**
     * Calculates the daily salary for a given record, considering normal pay, premiums, and overtime.
     */
    fun calculateDailySalary(record: DailyWorkRecord): Float {
        val hourlyRate = payrollConfig.dailyRate / payrollConfig.maxHours

        if (record.isAbsent) return 0.0f

        var salary = payrollConfig.dailyRate

        if (record.dayType != "Normal" || record.isNightShift) {
            salary = calculatePremiumPay(record, hourlyRate)
        }

        if (record.regOvertimeHrs > 0 || record.nsHrs > 0) {
            salary += calculateOvertimePay(record, hourlyRate)
        }

        return salary
    }

    /**
     * Calculates premium pay for holidays, rest days, and night shifts.
     */
    private fun calculatePremiumPay(record: DailyWorkRecord, hourlyRate: Float): Float {
        // Base pay calculation
        val basePay = payrollConfig.maxHours * hourlyRate * (payRates[record.dayType] ?: 1.0f)

        // Night shift bonus
        val nightShiftBonus = if (record.isNightShift) {
            record.nsHrs * hourlyRate * 1.10f
        } else 0.0f

        return basePay + nightShiftBonus
    }

    /**
     * Calculates overtime pay for regular and night shift overtime hours.
     */
    private fun calculateOvertimePay(record: DailyWorkRecord, hourlyRate: Float): Float {
        // Regular overtime pay
        val regOvertimePay = record.regOvertimeHrs * hourlyRate * (overtimeRates[record.dayType] ?: 1.0f)

        // Night shift overtime pay
        val nsOvertimePay = record.nsOvertimeHrs * hourlyRate * (nightShiftRates[record.dayType] ?: 1.0f)

        return regOvertimePay + nsOvertimePay
    }

    /**
     * Resets payrollConfig to default values using the Builder.
     */
    fun resetPayrollConfig() {
        payrollConfig = PayrollConfig.Builder().build()
    }
}
