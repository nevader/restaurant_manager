package UI;

import Units.RestaurantManage;

public class MenagerInterface extends UserInterface{

    private final RestaurantManage restaurantManage;

    public MenagerInterface() {
        restaurantManage = new RestaurantManage();
    }

    public void manageRestaurant() {

        _manager();;
        System.out.println("\nCzym chcesz zarządzać?\n" +
                ".---------------.\n" +
                "| #1 Menu       |\n" +
                "| #2 Zamowienia |\n" +
                "| #3 Personel   |\n" +
                "| #4 Finanse    |\n" +
                "| #0 Cofnij     |\n" +
                "'---------------'\n");


        do {
            flag = false;
            userChoice = userInputNextInt("Wybierz opcje: \n#");

            switch (userChoice) {
                case 1:
                    flag = true;
                    zarzadzajMenu();
                    break;
                case 2:
                    System.out.println("zamowienia");
                    restaurantManage.printOrders();
                    pressAnyKeyToContinue();
                    manageRestaurant();
                    break;
                case 3:
                    zarzadzajPersonelem();
                    manageRestaurant();
                    break;
                case 4:
                    break;
                case 0:
                    flag = true;
                    break;
                default:
                    wybierzPoprawna();
                    break;
            }
        } while (!flag);
    }
    public void zarzadzajMenu() {

        _menu();
        System.out.println("\n"+
                ".--------------------------.\n" +
                "| #1 Zarzadzaj daniami     |\n" +
                "| #2 Zarzadzaj kategoriami |\n" +
                "| #3 Wyswietl menu         |\n" +
                "| #0 Cofnij                |\n" +
                "'--------------------------'\n");

        do {
            userChoice = userInputNextInt("Wybierz opcje: \n#");
            flag = false;
            switch (userChoice) {
                case 1:
                    flag = true;
                    zarzadzajDaniami();
                    break;
                case 2:
                    break;
                case 3:
                    menuManage.printMenu();
                    pressAnyKeyToContinue();
                    zarzadzajMenu();
                    break;
                case 0:
                    flag = true;
                    manageRestaurant();
                    break;
                default:
                    wybierzPoprawna();
            }
        } while (!flag);

    }
    public void zarzadzajDaniami() {

        _dania();
        System.out.println("\n"+
                ".----------------------------------.\n" +
                "| #1 Dodaj nowe danie              |\n" +
                "| #2 Usun danie                    |\n" +
                "| #3 Zapisz menu w pliku           |\n" +
                "| #4 Wczytaj menu z pliku          |\n" +
                "| #5 Wyswietl liste wszystkich dań |\n" +
                "| #0 Cofnij                        |\n" +
                "'----------------------------------'\n");

        do {
            userChoice = userInputNextInt("Wybierz opcje: \n#");
            flag = false;

            switch (userChoice) {
                case 1:
                    restaurantManage.addItem();
                    zarzadzajDaniami();
                    break;
                case 2:
                    restaurantManage.removeItem();
                    zarzadzajDaniami();
                    break;
                case 3:
                    menuManage.saveMenuItemsToFile();
                    zarzadzajDaniami();
                    break;
                case 4:
                    menuManage.loadMenuItemsFromFile();
                    break;
                case 5:
                    _dania();
                    menuManage.printMenuItems();
                    pressAnyKeyToContinue();
                    zarzadzajDaniami();
                case 0:
                    flag = true;
                    zarzadzajMenu();
                    break;
                default:
                    wybierzPoprawna();

            }
        } while (!flag);

    }
    public void zarzadzajPersonelem() {

        _personel();

        System.out.println("\n" +
                ".---------------------------------------.\n" +
                "| #1 Zatrudnij nowego pracownika        |\n" +
                "| #2 Zwolnij aktualnego pracownika      |\n" +
                "| #3 Pokaz liste wszystkich pracownikow |\n" +
                "| #0 Cofnij                             |\n" +
                "'---------------------------------------'\n");

        do {
            flag = false;
            userChoice = userInputNextInt("Wybierz opcje: \n#");

            switch (userChoice) {
                case 1:
                    restaurantManage.addNewEmploy();
                    zarzadzajPersonelem();
                    break;
                case 2:
                    break;
                case 3:
                    personelManage.printListaPracownikow();
                    pressAnyKeyToContinue();
                    zarzadzajPersonelem();
                    break;
                case 0:
                    flag = true;
                    break;
                default:
                    wybierzPoprawna();
                    break;
            }
        } while (!flag);
    }
}