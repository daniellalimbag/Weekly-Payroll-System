data class PayrollConfig private constructor(
    val dailyRate: Float,
    val maxHours: Int,
    val workdays: Int,
    val defDayType: String,
    val defInTime: String,
    val defOutTime: String
) {
    class Builder {
        private var dailyRate: Float = 500.0F
        private var maxHours: Int = 0
        private var workdays: Int = 2
        private var defDayType: String = "Normal"
        private var defInTime: String = "0900"
        private var defOutTime: String = "0900"

        fun dailyRate(dailyRate: Float) = apply { this.dailyRate = dailyRate }
        fun maxHours(maxHours: Int) = apply { this.maxHours = maxHours }
        fun workdays(workdays: Int) = apply { this.workdays = workdays }
        fun defDayType(defDayType: String) = apply { this.defDayType = defDayType }
        fun defInTime(defInTime: String) = apply { this.defInTime = defInTime }
        fun defOutTime(defOutTime: String) = apply { this.defOutTime = defOutTime }

        fun build() = PayrollConfig(dailyRate, maxHours, workdays, defDayType, defInTime, defOutTime)
    }
}