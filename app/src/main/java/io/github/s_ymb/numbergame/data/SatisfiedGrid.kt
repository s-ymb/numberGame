package io.github.s_ymb.numbergame.data

data class SatisfiedGrid (
    // TODO 日付データの扱いを日付型にする
    val createDt: String = "",
    val createUser: String = "",
    val data: Array<IntArray> = Array(NumbergameData.NUM_OF_ROW){IntArray(NumbergameData.NUM_OF_COL){NumbergameData.NUM_NOT_SET}}
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SatisfiedGrid

        return data.contentDeepEquals(other.data)
    }

    override fun hashCode(): Int {
        return data.contentDeepHashCode()
    }
}


