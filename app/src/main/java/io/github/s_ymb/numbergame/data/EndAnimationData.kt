package io.github.s_ymb.numbergame.data

data class EndAnimationData(
    var name:String,
    var init: Int,
    var duration: Long,
    var data: Array<Array<LongArray>>,
) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as EndAnimationData

            return data.contentDeepEquals(other.data)
        }

        override fun hashCode(): Int {
            return data.contentDeepHashCode()
        }
}