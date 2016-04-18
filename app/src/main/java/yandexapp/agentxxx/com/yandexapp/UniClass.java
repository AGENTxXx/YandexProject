package yandexapp.agentxxx.com.yandexapp;


public class UniClass {

    public static String[] endingAlbum = {"альбом","альбома","альбомов"};
    public static String[] endingTrack = {"песня","песни","песен"};

    /**
     * Функция возвращает окончание для множественного числа слова на основании числа и массива окончаний
     * param  number Integer Число на основе которого нужно сформировать окончание
     * param  endingsArray  Array Массив слов или окончаний для чисел (1, 4, 5),
     *         например {'яблоко', 'яблока', 'яблок'}
     * return String
     */
    public static String getNumEnding(int number, String[] endingArray)
    {
        String ending = "";
        number = number % 100;
        if (number>=5 && number<=20) {
            ending=endingArray[2];
        }
        else {
            number = number % 10;
            switch (number)
            {
                case (1): ending = endingArray[0]; break;
                case (2):
                case (3):
                case (4): ending = endingArray[1]; break;
                default: ending=endingArray[2];
            }
        }
        return ending;
    }


}
