package com.credential.cubrism.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.credential.cubrism.R
import com.credential.cubrism.view.adapter.CalMonth
import com.credential.cubrism.view.adapter.DateSelect
import com.credential.cubrism.view.adapter.Goals
import com.credential.cubrism.view.adapter.QnaData

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
        addQuestion(
            QnaData("정보처리기사", "제목1", R.drawable.qna_photo,
            "글 1입니다.", "11:00", "안해연")
        )
        addQuestion(
            QnaData("제빵왕기능사", "제목2", R.drawable.qna_photo,
            "글 2입니다. 근데 이런 자격증이 있나요?\n글쎄요. 제가 만들면 있는 겁니다.\n- 익명의 사나이 -", "12:00", "황윤구")
        )
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

//class StudyListViewModel : ViewModel() { // 스터디 그룹 페이지 항목 리스트 viewmodel
//    private val _studyList = MutableLiveData<ArrayList<StudyList>>(arrayListOf())
//    val studyList: LiveData<ArrayList<StudyList>> get() = _studyList // 읽기만 가능(get)
//
//    val sampleInfo = "스터디 소개글입니다.\n스터디 소개글입니다.스터디소개글입니다스터디소개글입니다스터디소개글입니다"
//    val sampleTags = ArrayList<Tags>().apply {
//        add(Tags("#널널함"))
//        add(Tags("#열공"))
//    }
//
//    init {
//        addList(StudyList("정처기 삼일컷 스터디", sampleInfo, sampleTags, 4, 4))
//        addList(StudyList("토익 토플 토스 오픽 일주일컷 스터디", sampleInfo, sampleTags, 10, 1))
//    }
//
//    fun addList(value: StudyList) { // 질문 추가
//        _studyList.value?.add(value)
//        _studyList.value = _studyList.value // 옵서버 에게 변경 사항을 알림
//    }
//
//    fun deleteList(value: StudyList) {
//        _studyList.value?.remove(value)
//        _studyList.value = _studyList.value // 옵서버 에게 변경 사항을 알림
//    }
//}

class GoalListViewModel : ViewModel() {
    private val _goalList = MutableLiveData<ArrayList<Goals>>(arrayListOf())
    val goalList: LiveData<ArrayList<Goals>> get() = _goalList

    init {
        addList(Goals("목표 1입니다.", "0시간 0분", 1))
    }
    fun addList(value: Goals) {
        _goalList.value?.add(value)
        _goalList.value = _goalList.value
    }
}

class DDayViewModel : ViewModel() {
    private val _pairStringLiveData = MutableLiveData<Pair<String, String>>()
    val pairStringLiveData: LiveData<Pair<String, String>> get() = _pairStringLiveData

    // Pair<String, String> 값을 설정하는 함수
    fun setPairString(pair: Pair<String, String>) {
        _pairStringLiveData.value = pair
    }
}

class TitleViewModel : ViewModel() {
    // 초기값을 보관하는 변수
    private val _initialValue = "스터디 환영글이다!!!"
    private val _editTextValue = MutableLiveData<String>()

    // LiveData를 통해 초기값을 전달하는 메서드
    val editTextValue: LiveData<String> = _editTextValue

    init {
        // 초기값을 LiveData에 설정
        setEditTextValue(_initialValue)
    }

    // EditText의 값을 저장하는 메서드
    fun setEditTextValue(value: String) {
        _editTextValue.value = value
    }
}