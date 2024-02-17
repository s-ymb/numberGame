package com.s_ymb.numbergame.data

import java.text.SimpleDateFormat

class SatisfiedGriListInit
{
    companion object {

        /*
                SatisfiedGridArrauInit に登録されている初期データより初期データリストを作成する
         */
        public fun getInitialListData(): MutableList<SatisfiedGrid> {
            val initData: MutableList<SatisfiedGrid> = mutableListOf()
            var satisfy: SatisfiedGrid
            //    val initDataCnt = SatisfiedGridArrayInit.data.size
            SatisfiedGridArrayInit.data.forEach {
                val formatter = SimpleDateFormat("yyyy/mm/dd")
                val dateString = "2024/02/01"
                val date = formatter.parse(dateString)
                initData.add(
                    SatisfiedGrid(
                        createDt = dateString,
                        createUser = "Default",
                        data = it
                    )
                )
            }
            return initData
        }
    }
}