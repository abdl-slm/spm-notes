package com.salam94.spmnotes.ui.pastyear

import androidx.lifecycle.ViewModel
import com.salam94.spmnotes.adapter.NotesAdapter
import com.salam94.spmnotes.model.Notes
import com.salam94.spmnotes.util.*

class PastYearViewModel : ViewModel() {
    private val subjects = ArrayList<Notes>()

    fun loadNotes(pastYearNavigator: PastYearNavigator): NotesAdapter {

        subjects.add(Notes("Additional Mathematics Form 5", NOTES_URL + SENARAI_FORMULA_ADDMATH_FORM_5))
        subjects.add(Notes("Chemistry Form 4", NOTES_URL + FAQ_CHEMISTRY_FORM4))
        subjects.add(Notes("Physics Form 4 & 5", NOTES_URL + FORM_4_5_FIZIK))
        subjects.add(Notes("Physics Formula List", NOTES_URL + SENARAI_FORMULA_FIZIK))
        subjects.add(Notes("Mathematics Form 4", NOTES_URL + FORM_4_MATH))
        subjects.add(Notes("Mathematics Form 5", NOTES_URL + FORM_4_MATH))
        subjects.add(Notes("Sejarah Form 4", NOTES_URL + FORM_4_SEJARAH))
        subjects.add(Notes("Sejarah Form 5", NOTES_URL + FORM_5_SEJARAH))
        subjects.add(Notes("Bahasa Melayu (Nota Tambahan)", NOTES_URL + NOTA_BM))
        subjects.add(Notes("Bahasa Melayu (Nota Tatabahasa)", NOTES_URL + TATABAHASA_BM))
        subjects.add(Notes("Biology", NOTES_URL + FORM_4_5_BIO))

        return NotesAdapter(subjects, pastYearNavigator)
    }
}