package com.credential.cubrism

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CalMonthViewModel : ViewModel() {
    private val _calMonthList = MutableLiveData<ArrayList<CalMonth>>(arrayListOf())
    private val _isScheduledLiveData = MutableLiveData<ArrayList<DateSelect>>(arrayListOf())

    val calMonthList: LiveData<ArrayList<CalMonth>> get() = _calMonthList // 읽기만 가능(get)
    val isScheduledLiveData: LiveData<ArrayList<DateSelect>> get() = _isScheduledLiveData

    fun addDateMonth(value: CalMonth) { // 추가
        _calMonthList.value?.add(value)
        _calMonthList.value = _calMonthList.value // 옵서버 에게 변경 사항을 알림
    }

    fun deleteDateMonth(value: CalMonth) { // 삭제
        _calMonthList.value?.remove(value)
        _calMonthList.value = _calMonthList.value
    }

    fun modifyDateMonth(value: CalMonth, newValue: CalMonth) { // 수정
        val updatedList = _calMonthList.value?.toMutableList() ?: mutableListOf()

        // value가 리스트에 포함되어 있다면 해당 인덱스를 찾아 newValue로 대체
        val index = updatedList.indexOf(value)
        if (index != -1) {
            _calMonthList.value?.set(index, newValue)
            _calMonthList.value = _calMonthList.value
        }
    }

//    fun updateIsScheduled(item: DateSelect, isScheduled: Boolean) {
//        _isScheduledLiveData.value = isScheduled
//    }
}

class QnaListViewModel : ViewModel() {
    private val _questionList = MutableLiveData<ArrayList<QnaData>>(arrayListOf())
    val questionList: LiveData<ArrayList<QnaData>> get() = _questionList // 읽기만 가능(get)

    init {
        addQuestion(QnaData("정보처리기사", "제목1", R.drawable.qna_photo,
            "글 1입니다.", "11:00", "안해연"))
        addQuestion(QnaData("제빵왕기능사", "제목2", R.drawable.qna_photo,
            "글 2입니다. 근데 이런 자격증이 있나요?\n글쎄요. 제가 만들면 있는 겁니다.\n- 익명의 사나이 -", "12:00", "황윤구"))
    }

    fun addQuestion(value: QnaData) { // 질문 추가
        _questionList.value?.add(value)
        _questionList.value = _questionList.value // 옵서버 에게 변경 사항을 알림
    }

    fun deleteQuestion(value: QnaData) { // 질문 삭제
        _questionList.value?.remove(value)
        _questionList.value = _questionList.value
    }
}
//
//class TodoViewModel : ViewModel() {
//    val itemList: MutableLiveData<List<TodayData>> = MutableLiveData()
//
//    // 아이템 상태 업데이트
//    fun updateTodo(position: Int, isChecked: Boolean) {
//        val currentList = itemList.value.orEmpty().toMutableList()
//        currentList[position].todayCheck = isChecked
//        itemList.value = currentList
//    }
//}

class StudyListViewModel : ViewModel() { // 스터디 그룹 페이지 항목 리스트 viewmodel
    private val _studyList = MutableLiveData<ArrayList<String>>(arrayListOf())
    val studyList: LiveData<ArrayList<String>> get() = _studyList // 읽기만 가능(get)

    init {
        addList("정처기 삼일컷 스터디")
        addList("토익 토플 토스 오픽 일주일컷 스터디")
    }

    fun addList(value: String) { // 질문 추가
        _studyList.value?.add(value)
        _studyList.value = _studyList.value // 옵서버 에게 변경 사항을 알림
    }
}

class MyStudyViewModel : ViewModel() { // 나의 스터디 항목 리스트 viewmodel
    private val _studyList = MutableLiveData<ArrayList<String>>(arrayListOf())
    val studyList: LiveData<ArrayList<String>> get() = _studyList // 읽기만 가능(get)

    init {
        addList("정처기 삼일컷 스터디")
        addList("토익 토플 토스 오픽 일주일컷 스터디")
    }

    fun addList(value: String) { // 질문 추가
        _studyList.value?.add(value)
        _studyList.value = _studyList.value // 옵서버 에게 변경 사항을 알림
    }
}