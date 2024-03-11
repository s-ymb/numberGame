package io.github.s_ymb.numbergame.data

/**
 *     NumberGame の定義を満たす数列のクラスの配列
 *
 */
class SatisfiedGridList : NumbergameData() {

    private val dataList: MutableList<SatisfiedGridData> = SatisfiedGriListInit.getInitialListData()

    fun add(satisfied :SatisfiedGridData = SatisfiedGridData(SatisfiedGrid()) ){
        dataList.add(satisfied)
    }



    /*
        正解リストのサイズを返却する
     */
    fun getSize() :Int{
        return dataList.size
    }
    /*
        指定インデックスの正解データを返却する
    */
    fun getSatisfied(index: Int) :SatisfiedGridData{
        return dataList[index]
    }

}


