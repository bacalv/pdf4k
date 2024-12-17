package io.pdf4k.domain

import io.pdf4k.extensions.singleLine
import java.time.LocalDate

data class Musician(
    val name: String,
    val image: String,
    val bio: String,
    val dob: LocalDate,
    val address: String,
    val wikiLink: String
) {
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
                address = "25 Brook Street, W1K 4HB",
                wikiLink = "https://en.wikipedia.org/wiki/Jimi_Hendrix"
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
                address = "1 Globe House, N8 8PN",
                wikiLink = "https://en.wikipedia.org/wiki/David_Gilmour"
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
                address = "Ballyshannon, County Donegal",
                wikiLink = "https://en.wikipedia.org/wiki/Rory_Gallagher"
            ),
            Musician(
                name = "Frank Zappa",
                image = "zappa.png",
                bio = """
                    As a mostly self-taught composer and performer, Zappa had diverse musical influences that led him 
                    to create music that was sometimes difficult to categorize. While in his teens, he acquired a taste 
                    for 20th-century classical modernism, African-American rhythm and blues, and doo-wop music.[7] He 
                    began writing classical music in high school, while simultaneously playing drums in rhythm and 
                    blues bands, later switching to electric guitar. His debut studio album with the Mothers of 
                    Invention, Freak Out! (1966), combined satirical but seemingly conventional rock and roll songs 
                    with extended sound collages. He continued this eclectic and experimental approach throughout his 
                    career.
                """.singleLine(),
                dob = LocalDate.parse("1940-12-21"),
                address = "Baltimore, Maryland, U.S.",
                wikiLink = "https://en.wikipedia.org/wiki/Frank_Zappa"
            ),
            Musician(
                name = "Debbie Harry",
                image = "blondie.png",
                bio = """
                    Deborah Ann Harry (born Angela Trimble; July 1, 1945) is an American singer, songwriter and actress
                    best known as the lead vocalist of the band Blondie. Four of her songs with the band reached 
                    No. 1 on the US charts between 1979 and 1981.  Born in Miami, Florida, Harry was adopted as an 
                    infant and raised in Hawthorne, New Jersey. After college she worked various jobs—as a dancer, 
                    a Playboy Bunny, and a secretary (including at the BBC in New York)—before her breakthrough in 
                    the music industry
                """.singleLine(),
                dob = LocalDate.parse("1946-06-01"),
                address = "Miami, Florida, U.S..",
                wikiLink = "https://en.wikipedia.org/wiki/Debbie_Harry"
            )
        )
    }
}