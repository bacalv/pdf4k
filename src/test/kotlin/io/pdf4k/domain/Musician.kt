package io.pdf4k.domain

import io.pdf4k.extensions.singleLine
import java.time.LocalDate

data class Musician(val name: String, val image: String, val bio: String, val dob: LocalDate, val address: String) {
    companion object {
        val musicians = listOf(
            Musician(
                name = "Jimi Hendrix",
                image ="hendrix.png",
                bio = """
                    Born in Seattle, Washington, Hendrix began playing guitar at age 15. In 1961,
                    he enlisted in the US Army, but was discharged the following year.
                    Soon afterward, he moved to Clarksville, then Nashville, Tennessee, and began 
                    playing gigs on the chitlin' circuit, earning a place in the Isley Brothers'
                    backing band and later with Little Richard, with whom he continued to work
                    through mid-1965. He then played with Curtis Knight and the Squires before
                    moving to England in late 1966 after bassist Chas Chandler of the Animals became
                    his manager. Within months, Hendrix had earned three UK top ten hits with his
                    band
                """.singleLine(),
                dob = LocalDate.parse("1942-10-27"),
                address = "25 Brook Street, W1K 4HB"
            ),
            Musician(
                name = "David Gilmour",
                image ="gilmour.png",
                bio = """
                    As a member of Pink Floyd, Gilmour was inducted into the US Rock and Roll Hall
                    of Fame in 1996, and the UK Music Hall of Fame in 2005. In 2003, Gilmour was
                    made a Commander of the Order of the British Empire (CBE). He received the award
                    for Outstanding Contribution at the 2008 Q Awards.[4] In 2023, Rolling Stone
                    named him the 28th-greatest guitarist.
                """.singleLine(),
                dob = LocalDate.parse("1946-03-06"),
                address = "1 Globe House, N8 8PN"
            ),
            Musician(
                name = "Rory Gallagher",
                image = "gallagher.jpeg",
                bio = """
                    In 1966, Gallagher formed the blues rock power trio Taste, which experienced moderate
                    commercial success and popularity in the United Kingdom. After the dissolution of Taste,
                    Gallagher pursued a solo career, releasing music throughout the 1970s and 1980s and selling
                    more than 30 million records worldwide.
                """.singleLine(),
                dob = LocalDate.parse("1948-03-02"),
                address = "Ballyshannon, County Donegal"
            )
        )
    }
}