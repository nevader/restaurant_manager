package Units;

import Entities.DeliveryMan;
import Entities.Waiter;
import Enums.Status;

import UI.UserInterface;
import DataTypes.Address;
import Entities.Customer;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class OrdersManage extends UserInterface {

    public static ArrayList <Order> allOrders;

    /*listy zamowien do ugotowania*/
    public static ArrayList <Order> deliveryOrdersToCook;
    public static ArrayList <Order> tableOrdersToCook;

    /*listy gotowych zamowien do dostarczenia*/
    public static ArrayList <Order> deliveryOrdersForDelivery;
    public static ArrayList <Order> tableOrdersForDelivery;

    /*lista zrealizowanych/anulowanych zamowien*/
    public static ArrayList <Order> completedOrders;

    /*koszyk klienta*/
    private final ArrayList <MenuManage.MenuItem> koszyk = new ArrayList<>();

    private static long lastOrderCooked;
    private static long lastOrderDelivered;
    private static long lastOrderPlaced;
    private static boolean diferentCooking;

    /*Czas dostawy zamowienia przed dostawce*/
    private static final long deliverytime = 10000 ;

    /*Czas przyzadzenia jednego dania przez jednego kucharza*/
    private static final long prepareTime = 5000;

    /*Czas przedawienia sie zamowienia*/
    public static final long expiredOrder = 30000;

    /*Czas rezerwacji stolika*/
    public static final long tableReservationTime = 10000;

    public OrdersManage() {
        allOrders = new ArrayList<>();
        deliveryOrdersToCook = new ArrayList<>();
        tableOrdersToCook = new ArrayList<>();
        completedOrders = new ArrayList<>();
        deliveryOrdersForDelivery = new ArrayList<>();
        tableOrdersForDelivery = new ArrayList<>();
    }


    public void randomOrder() {

        clearKoszyk();
        Address address = new Address("Random Street 12", "Random City", "000-000");

        int temp = (Math.random() <= 0.5) ? 1 : 2;

        boolean delivery = temp == 1;
        int listadan = menuManage.getItemList().size();
        int iloscpozycji = (int) Math.floor(Math.random()*(3-1+1)+1);


        for (int i = 0; i < iloscpozycji; i++) {
          int idpozycij = (int) Math.floor(Math.random()*(listadan-1+1)+1);
          int iloscdan = (int) Math.floor(Math.random()*(2-1+1)+1);

            for (int j = 0; j < menuManage.getItemList().size(); j++) {
                if (menuManage.getItemList().get(j).getMenuItemID() == idpozycij) {
                    for (int k = 0; k < iloscdan; k++) {
                        koszyk.add(menuManage.getItemList().get(j));
                    }
                }
            }
        }

        Customer customer = new Customer("Random", "666-666-666", address, 0);
        if (delivery) {
            newOrderDelivery(customer, koszyk);
        } else {
            newOrderRestaurant(customer, koszyk);
        }

        startProcess();
        _koszyk();
        showKoszyk(koszyk);
        System.out.println(delivery ? "W dostawie" : "W restauracji");






    }

    /*Sekcja skladania nowych zamowien w dostawie*/
    public void newOrderDelivery() {

        do {
            flag = false;
            _dostawa();
            System.out.println("\n" +
                    ".------------------------.\n" +
                    "| #1 Dodaj do koszyka    |\n" +
                    "| #2 Usun z koszyka      |\n" +
                    "| #3 Pokaz koszyk        |\n" +
                    "| #4 Realizuj zamowienie |\n" +
                    "| #0 Wyjdz               |\n" +
                    "'------------------------'\n");
            userChoice = userInputNextInt("Wybierz:\n#");

            switch (userChoice) {
                case 1:
                    koszyk.addAll(addItemsDoKosztka());
                    newOrderDelivery();
                    break;
                case 2:
                    koszykRemove();
                    newOrderDelivery();
                    break;
                case 3:
                    _koszyk();
                    showKoszyk(koszyk);
                    pressAnyKeyToContinue();
                    newOrderDelivery();
                    break;
                case 4:
                    realizacjaZamowieniaDostawa();
                    flag = true;
                    break;
                case 0:
                    flag = true;
                    break;
            }


        } while (!flag);

    }
    public Customer daneDoDostawy() {
        Scanner in = new Scanner(System.in);

        String street;
        String city;
        String zipcode;

        String imie;
        String telefon;

        _dostawa();
        System.out.println(
                "\n.---------------------------------------.\n" +
                        "| Wypelnij formularz dotyczacy dostawy. |\n" +
                        "'---------------------------------------'\n");
        System.out.println("Podaj swoje imie: ");
        System.out.print("#");
        imie = in.nextLine();

        _dostawa();
        System.out.println("\nWitaj, " + imie);
        System.out.println(
                ".---------------------------------------.\n" +
                        "| Wypelnij formularz dotyczacy dostawy. |\n" +
                        "'---------------------------------------'\n");
        System.out.println("Podaj swoj numer telefonu: ");
        System.out.print("#");

        telefon = in.nextLine();

        _dostawa();
        System.out.println("\nWitaj, " + imie);
        System.out.println(
                ".---------------------------------------.\n" +
                        "| Wypelnij formularz dotyczacy dostawy. |\n" +
                        "'---------------------------------------'\n");
        System.out.println("Podaj swoje miasto: ");
        System.out.print("#");

        city = in.nextLine();

        _dostawa();
        System.out.println("\nWitaj, " + imie);
        System.out.println(
                ".---------------------------------------.\n" +
                        "| Wypelnij formularz dotyczacy dostawy. |\n" +
                        "'---------------------------------------'\n");
        System.out.println("Podaj swój kod pocztowy: ");
        System.out.print("#");

        zipcode = in.nextLine();

        _dostawa();
        System.out.println("\nWitaj, " + imie);
        System.out.println(
                ".---------------------------------------.\n" +
                        "| Wypelnij formularz dotyczacy dostawy. |\n" +
                        "'---------------------------------------'\n");
        System.out.println("Podaj swoja ulice: ");
        System.out.print("#");

        street = in.nextLine();


        Address address = new Address(street, city, zipcode);

        return new Customer(imie, telefon, address, 0);

    }
    public void realizacjaZamowieniaDostawa() {
        Customer customer = daneDoDostawy();

        _podsumowanie();
        System.out.println();
        showKoszyk(koszyk);


        System.out.println("\n" +
                ".------------------------.\n" +
                "| Twoje dane do dostawy: |\n" +
                "'------------------------'");

        System.out.println("Imie: " + customer.getName());
        System.out.println("Telefon: " + customer.getPhone());
        System.out.println("Ulica: " + customer.getAddress().getStreetAddress());
        System.out.println("Kod pocztowy: " + customer.getAddress().getZipCode());
        System.out.println("Miasto: " + customer.getAddress().getCity());


        newOrderDelivery(customer, koszyk);
        startProcess();
        pressAnyKeyToContinue();

    }
    public void newOrderDelivery(Customer customer, ArrayList<MenuManage.MenuItem> orderedItems) {
        double price = 0;
        for (int i = 0; i < orderedItems.size(); i++) {
            price += orderedItems.get(i).getPrice();
        }
        Order Order = new Order(true, Status.PRZYJETE.toString(), customer, orderedItems, price);
        allOrders.add(Order);


    }

    /*Sekcja do zarzadzania koszykiem*/
    public ArrayList<MenuManage.MenuItem> addItemsDoKosztka () {

        ArrayList <MenuManage.MenuItem> koszyk = new ArrayList<>();
        ArrayList <MenuManage.MenuItem> listaDan = menuManage.getItemList();

        menuManage.printMenu();


        do {
            flag = false;
            System.out.print("\n" +
                    ".-----------------------------------------------------------.\n" +
                    "| Wybierz danie które chcesz zamowic wypisujac jego #numer. |\n" +
                    "'-----------------------------------------------------------'\n");
            userChoice = userInputNextInt("\nWpisz #numer dania: \n#");

            if (userChoice < 1 || userChoice > listaDan.size()) {
                menuManage.printMenu();
                System.out.println();
                System.out.println("Wpisz poprawny numer!");

            }  else {
                for (int i = 0; i < listaDan.size() || !flag; i++) {
                    if (listaDan.get(i).getMenuItemID() == userChoice &&
                    !listaDan.get(i).isAvalible()) {
                        menuManage.printMenu();
                        System.out.println();
                        System.out.print("Wybrane danie nie jest dostepne, wybierz inne.");
                        break;
                    }
                    else if (listaDan.get(i).getMenuItemID() == userChoice) {
                        int ilosc = userInputNextInt("\nPodaj ilość porcji: \n#");
                        for (int j = 0; j < ilosc; j++) {
                            koszyk.add(listaDan.get(i));
                            flag = true;
                        }

                    }
                }
            }
        } while (!flag);

        return koszyk;

    }
    public void showKoszyk (ArrayList <MenuManage.MenuItem> koszyk) {

        double totalprice = 0;
        System.out.println(
                ".----------------------------.\n" +
                "| Twoje aktualne zamówienie: |\n" +
                "'----------------------------'");
        for (int i = 0; i < koszyk.size(); i++) {
            System.out.print("#" + (i+1) + " - " + koszyk.get(i).getName() +
                    " - $" + koszyk.get(i).getPrice() + "\n");

            totalprice += koszyk.get(i).getPrice();
        }

        System.out.printf("Całkowita cena: $%,.2f", totalprice);
        System.out.println();


    }
    public void clearKoszyk() {
        koszyk.clear();
    }
    public boolean removeItemKoszyk (int id) {

        String itemName = "";

        if (id > 0 && id < koszyk.size()+1) {
            itemName = koszyk.get(id-1).getName();

            System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n");
            System.out.println("\n" +
                    "██╗░░░██╗░██████╗██╗░░░██╗███╗░░██╗  ██████╗░░█████╗░███╗░░██╗██╗███████╗\n" +
                    "██║░░░██║██╔════╝██║░░░██║████╗░██║  ██╔══██╗██╔══██╗████╗░██║██║██╔════╝\n" +
                    "██║░░░██║╚█████╗░██║░░░██║██╔██╗██║  ██║░░██║███████║██╔██╗██║██║█████╗░░\n" +
                    "██║░░░██║░╚═══██╗██║░░░██║██║╚████║  ██║░░██║██╔══██║██║╚████║██║██╔══╝░░\n" +
                    "╚██████╔╝██████╔╝╚██████╔╝██║░╚███║  ██████╔╝██║░░██║██║░╚███║██║███████╗\n" +
                    "░╚═════╝░╚═════╝░░╚═════╝░╚═╝░░╚══╝  ╚═════╝░╚═╝░░╚═╝╚═╝░░╚══╝╚═╝╚══════╝");
            System.out.println("\nUsunałes:\n" + "#" + id + " | " + itemName + "\n");

            koszyk.remove(id-1);
            return true;
        } else {
            return false;
        }
    }
    public void koszykRemove() {

        _usunDanie();

        if (koszyk.isEmpty()) {
            System.out.print("\n" +
                    ".-------------------------.\n" +
                    "| Twój koszyk jest pusty! |\n" +
                    "'-------------------------'\n");

            pressAnyKeyToContinue();
            return;
        }

        System.out.println("\n" +
                ".---------------------------------------------------------.\n" +
                "| Wybierz danie które chcesz usunąć wpisując jego #numer. |\n" +
                "'---------------------------------------------------------'");
        showKoszyk(koszyk);
        System.out.println();
        System.out.println("#0 Cofnij");

        userChoice = userInputNextInt("Wpisz #numer dania: \n#");

        if (userChoice == 0) {
            return;
        }

        boolean removed = removeItemKoszyk(userChoice);

        if (removed) {
            System.out.println(
                    ".-----------------------.\n" +
                            "| #1 Usun kolejne danie |\n" +
                            "| #0 Wróc               |\n" +
                            "'-----------------------'\n");

            do {
                userChoice = userInputNextInt("Wybierz opcje: \n#");
                switch (userChoice) {
                    case 1:
                        flag = true;
                        koszykRemove();
                        break;
                    case 0:
                        flag = true;
                        return;
                    default:
                        wybierzPoprawna();
                        break;
                }
            } while (!flag);
        }

    }

    /*Sekcja skladania nowych zamowien w restauracji i zarzadzania stolikami*/
    public void placeStacjonarne() {


        Scanner in = new Scanner(System.in);
        int stolik = rezerwujStolik();
        String imie;


        _kelner();

        System.out.println("\n" +
                ".---------------------------------------------.\n" +
                "|         Jezeli bedziesz juz gotowy,         |\n" +
                "| nacisnij Enter aby poprosic kelnera o menu. |\n" +
                "'---------------------------------------------'");

        pressAnyKeyToContinue();

        koszyk.addAll(addItemsDoKosztka());
        koszykstacjonar();

        _zamow();
        System.out.println("\n" +
                ".-------------------.\n" +
                "| Podaj swoje imie. |\n" +
                "'-------------------'\n");

        System.out.print("Wpisz:\n#");
        imie = in.nextLine();


        _koszyk();
        showKoszyk(koszyk);
        System.out.println("Dziekujemy!");
        Address address = new Address("", "", "");
        Customer customer = new Customer(imie, "", address, stolik);
        newOrderRestaurant(customer, koszyk);
        startProcess();
        pressAnyKeyToContinue();

    }
    public void koszykstacjonar() {

        do {
            flag = false;
            _menu();
            System.out.println("\n" +
                    ".---------------------------------------.\n" +
                    "| #1 Dodaj nastepne danie do zamowienia |\n" +
                    "| #2 Usuń pozycje z zamowienia          |\n" +
                    "| #3 Pokaz aktualnie zamowione dania    |\n" +
                    "| #4 Złóz zamowienie                    |\n" +
                    "'---------------------------------------'\n");
            userChoice = userInputNextInt("Wybierz:\n#");

            switch (userChoice) {
                case 1:
                    koszyk.addAll(addItemsDoKosztka());
                    koszykstacjonar();
                    break;
                case 2:
                    koszykRemove();
                    koszykstacjonar();
                    break;
                case 3:
                    _koszyk();
                    showKoszyk(koszyk);
                    pressAnyKeyToContinue();
                    koszykstacjonar();
                case 4:
                    flag = true;
                    break;
                default:
                    break;
            }


        } while (!flag);
    }
    public void newOrderRestaurant(Customer customer, ArrayList<MenuManage.MenuItem> orderedItems) {
        double price = 0;
        for (int i = 0; i < orderedItems.size(); i++) {
            price += orderedItems.get(i).getPrice();
        }

        Order Order = new Order(false, Status.PRZYJETE.toString(), customer, orderedItems, price);
        allOrders.add(Order);
    }
    public int rezerwujStolik() {
        do {
            flag = false;
            _rezerwuj();
            System.out.println();
            tables.printTables();
            System.out.println();

            if (userChoice == -1) {
                System.out.println("Podaj poprawny numer stolika!");
            } else if (userChoice == -2) {
                System.out.println("Twoj stolik jest zajęty! Wybierz inny: ");
            } else if (userChoice == -3) {
                System.out.println("Wybierz poprawny");
            }

            userChoice = userInputNextInt("Podaj numer stolika:\n#");

            if (userChoice < 1 || userChoice > 4) {
                userChoice = -1;
                continue;
            }

            switch (userChoice) {

                case 1:
                    if (!tables.getTable1().isAvalible()) {
                        userChoice = -2;
                        break;
                    } else {
                        tables.setTableNOt(1);
                        flag = true;
                        return userChoice;
                    }

                case 2:
                    if (!tables.getTable2().isAvalible()) {
                        userChoice = -2;
                        break;
                    } else {
                        tables.setTableNOt(2);
                        flag = true;
                        return userChoice;
                    }

                case 3:
                    if (!tables.getTable3().isAvalible()) {
                        userChoice = -2;
                        break;
                    } else {
                        tables.setTableNOt(3);
                        flag = true;
                        return userChoice;
                    }

                case 4:
                    if (!tables.getTable4().isAvalible()) {
                        userChoice = -2;
                        break;
                    } else {
                        tables.setTableNOt(4);
                        flag = true;
                        return userChoice;
                    }

                default:
                    userChoice = -3;
            }

        } while (!flag);

        return userChoice;
    }
    public void zwolnijStolik() {
        int stolik;
        for (int i = 0; i < completedOrders.size(); i++) {

            if (!completedOrders.get(i).isDelivery() && !completedOrders.get(i).getStatus().equals(Status.WYJSCIE.toString())) {
                stolik = completedOrders.get(i).getCustomer().getTable();

                System.out.println(new Date().getTime());

                if (!tables.isTableAvalible(stolik)) {
                    if ((completedOrders.get(i).getDaty().get(Status.FINISHED_DELIVERY_DATE.toString()).getTime() +
                    tableReservationTime) < new Date().getTime()) {
                        tables.setTableAvalible(stolik);
                        completedOrders.get(i).setStatus(Status.WYJSCIE.toString());
                    }
                }
            }
        }
    }


    /*Sortuje listy zamowien*/
    public static void startProcess() {
        sort();
        cook();
        sort();
        delivery();
        sort();
        place();
        sort();
    }
    public static void sort() {



        if (tableOrdersToCook.isEmpty() && tableOrdersForDelivery.isEmpty()) {
            lastOrderCooked = 0;
        }

        for (int i = 0; i < PersonelManage.listaDostawcow.size(); i++) {
            if (PersonelManage.listaDostawcow.get(i).getOrdersToDelivery().isEmpty()) {
                lastOrderDelivered = 0;
            }
            if (PersonelManage.listaKelnerow.get(i).getOrdersToPlace().isEmpty()) {
                lastOrderPlaced = 0;
            }
        }

        /*sortuje ukonczone zamowienia pod wzgledem czasu ich wydania*/
        completedOrders = (ArrayList<Order>) completedOrders.stream().sorted(Comparator.comparing(order -> order.getDaty()
                .get(Status.FINISHED_DELIVERY_DATE.toString()).getTime())).collect(Collectors.toList());
        

        /*przydziela kucharzom dania do przyzadzenia*/
        tableOrdersToCook = (ArrayList<Order>) allOrders.stream()
                .filter(order -> order.getStatus().equals(Status.PRZYJETE.toString()) &&
                        !order.isDelivery || order.getStatus().equals(Status.PRZEDAWNIONE.toString()) &&
                        !order.isDelivery).collect(Collectors.toList());

        /*przydziela kucharzom dania do przyzadzenia*/
        deliveryOrdersToCook = (ArrayList<Order>) allOrders.stream()
                .filter(order -> (order.getStatus().equals(Status.PRZYJETE.toString()) &&
                        order.isDelivery || order.getStatus().equals(Status.PRZEDAWNIONE.toString()) &&
                        order.isDelivery)).collect(Collectors.toList());



        /*sortuje liste dan do przyzadzenia po ID*/
        tableOrdersToCook = (ArrayList<Order>) tableOrdersToCook.stream()
                .sorted(Comparator.comparing(Order::getId)).collect(Collectors.toList());

        /*sortuje liste dan do przyzadzenia po ID*/
        deliveryOrdersToCook = (ArrayList<Order>) deliveryOrdersToCook.stream()
                .sorted(Comparator.comparing(Order::getId)).collect(Collectors.toList());




        /*sortuje liste dostawcow po tym jak duzo aktualnie maja dan do rozwiezienia*/
        PersonelManage.listaDostawcow = (ArrayList<DeliveryMan>) PersonelManage.listaDostawcow.stream()
                .sorted(Comparator.comparing(DeliveryMan::getOrdersToDeliverySize)).collect(Collectors.toList());

        PersonelManage.listaKelnerow = (ArrayList<Waiter>) PersonelManage.listaKelnerow.stream()
                .sorted(Comparator.comparing(Waiter::getOrdersToPlaceSize)).collect(Collectors.toList());



        /*przydziela zamowienia do dostarczenia dostawcom*/
        while (!deliveryOrdersForDelivery.isEmpty()) {
            PersonelManage.listaDostawcow.get(0).ordersToDelivery.add(deliveryOrdersForDelivery.get(0));
            deliveryOrdersForDelivery.remove(0);
            sort();
        }

        /*przydziala zamowienia do dostarczenia kelernom*/
        while (!tableOrdersForDelivery.isEmpty()) {
            PersonelManage.listaKelnerow.get(0).ordersToPlace.add(tableOrdersForDelivery.get(0));
            tableOrdersForDelivery.remove(0);
            sort();
        }


        /*check if expired*/
        if (!deliveryOrdersToCook.isEmpty()) {

            for (int i = 0; i < deliveryOrdersToCook.size(); i++) {

                if (!deliveryOrdersToCook.get(i).getStatus().equals(Status.PRZEDAWNIONE.toString())) {

                    long ordered = deliveryOrdersToCook.get(i).getDaty().get(Status.ORDERED_DATE.toString()).getTime();
                    long expired = ordered + expiredOrder;
                    long currtime = new Date().getTime();

                    if (currtime > expired) {
                        int temp = (int) Math.floor(Math.random()*(2-1+1)+1);


                        /*rezygnacja*/
                        if (temp == 2) {
                            deliveryOrdersToCook.get(i).getDaty().put(Status.ANULOWANE.toString(),
                                    deliveryOrdersToCook.get(i).getDaty().get(Status.ORDERED_DATE.toString()));

                            deliveryOrdersToCook.get(i).setStatus(Status.ANULOWANE.toString());
                            deliveryOrdersToCook.remove(i);
                            sort();
                        }

                        /*zrobic jako nastepne + znizka*/
                        else {
                            deliveryOrdersToCook.get(i).getDaty().put(Status.PRZEDAWNIONE.toString(),
                                    deliveryOrdersToCook.get(i).getDaty().get(Status.ORDERED_DATE.toString()));

                            deliveryOrdersToCook.get(i).setStatus(Status.PRZEDAWNIONE.toString());

                            deliveryOrdersToCook.get(i).priceDiscount();
                            sort();
                        }
                    }
                }
            }
        }
        if (!tableOrdersToCook.isEmpty()) {

            for (int i = 0; i < tableOrdersToCook.size(); i++) {

                if (!tableOrdersToCook.get(i).getStatus().equals(Status.PRZEDAWNIONE.toString())) {

                    long ordered = tableOrdersToCook.get(i).getDaty().get(Status.ORDERED_DATE.toString()).getTime();
                    long expired = ordered + expiredOrder;
                    long currtime = new Date().getTime();

                    if (currtime > expired) {
                        java.util.Random random = new java.util.Random();
                        int temp = (int) Math.floor(Math.random()*(2-1+1)+1);


                        /*rezygnacja*/
                        if (temp == 2) {
                            tableOrdersToCook.get(i).getDaty().put(Status.ANULOWANE.toString(),
                                    tableOrdersToCook.get(i).getDaty().get(Status.ORDERED_DATE.toString()));

                            tableOrdersToCook.get(i).setStatus(Status.ANULOWANE.toString());
                            tableOrdersToCook.remove(i);
                            sort();
                        }

                        /*zrobic jako nastepne + znizka*/
                        else {
                            tableOrdersToCook.get(i).getDaty().put(Status.PRZEDAWNIONE.toString(),
                                    tableOrdersToCook.get(i).getDaty().get(Status.ORDERED_DATE.toString()));

                            tableOrdersToCook.get(i).setStatus(Status.PRZEDAWNIONE.toString());

                            tableOrdersToCook.get(i).priceDiscount();
                            sort();
                        }
                    }
                }
            }
            for (int i = 0; i < tableOrdersToCook.size(); i++) {

                if (tableOrdersToCook.get(i).getStatus().equals(Status.PRZEDAWNIONE.toString())) {

                    Order temp = tableOrdersToCook.get(i);
                    int index = tableOrdersToCook.indexOf(temp);
                    tableOrdersToCook.remove(index);
                    tableOrdersToCook.add(0, temp);


                }
            }

            for (int i = 0; i < deliveryOrdersToCook.size(); i++) {

                if (deliveryOrdersToCook.get(i).getStatus().equals(Status.PRZEDAWNIONE.toString())) {

                    Order temp = deliveryOrdersToCook.get(i);
                    int index = deliveryOrdersToCook.indexOf(temp);
                    deliveryOrdersToCook.remove(index);
                    deliveryOrdersToCook.add(0, temp);

                }
            }

        }









    }
    public static void cook() {

        sort();

        long startedPraparing;
        long currentTime;
        long finisTime;
        long cookedTime;



        if (!OrdersManage.tableOrdersToCook.isEmpty() && !diferentCooking) {
            if (OrdersManage.tableOrdersToCook.get(0)
                    .getDaty().get(Status.STARTED_COOKING_DATE.toString()) == null) {
                if (lastOrderCooked != 0) {
                    startedPraparing = lastOrderCooked;
                    OrdersManage.tableOrdersToCook.get(0)
                            .getDaty().put(Status.STARTED_COOKING_DATE.toString(),
                                    new Date(startedPraparing));
                } else {
                    startedPraparing = new Date().getTime();
                    OrdersManage.tableOrdersToCook.get(0)
                            .getDaty().put(Status.STARTED_COOKING_DATE.toString(),
                                    new Date());
                }
            } else {
                startedPraparing = OrdersManage.tableOrdersToCook.get(0)
                        .getDaty().get(Status.STARTED_COOKING_DATE.toString()).getTime();
            }



            currentTime = new Date().getTime();

            finisTime = ((OrdersManage.tableOrdersToCook.get(0).getOrderedItems().size() * prepareTime) /
                    PersonelManage.listaKucharzy.size()) + startedPraparing;

            if (currentTime > finisTime) {
                OrdersManage.tableOrdersToCook.get(0).setStatus(Status.GOTOWE.toString());
                OrdersManage.tableOrdersToCook.get(0).setCookedDate(new Date(finisTime));
                OrdersManage.tableOrdersForDelivery.add(OrdersManage.tableOrdersToCook.get(0));
                cookedTime = finisTime - currentTime;
                OrdersManage.tableOrdersToCook.get(0).setCookedtime(cookedTime);
                OrdersManage.tableOrdersToCook.remove(0);
                lastOrderCooked = finisTime;
                startProcess();

                if (!OrdersManage.tableOrdersToCook.isEmpty()) {
                    OrdersManage.tableOrdersToCook.get(0).setSTARTEDCOOKING(new Date(finisTime));
                    startProcess();
                }

            }
        }

        if (!OrdersManage.deliveryOrdersToCook.isEmpty() && OrdersManage.tableOrdersToCook.isEmpty() ||
                diferentCooking) {
            if (OrdersManage.deliveryOrdersToCook.get(0)
                    .getDaty().get(Status.STARTED_COOKING_DATE.toString()) == null) {
                if (lastOrderCooked != 0) {
                    startedPraparing = lastOrderCooked;
                    OrdersManage.deliveryOrdersToCook.get(0)
                            .getDaty().put(Status.STARTED_COOKING_DATE.toString(),
                                    new Date(startedPraparing));
                } else {
                    startedPraparing = new Date().getTime();
                    OrdersManage.deliveryOrdersToCook.get(0)
                            .getDaty().put(Status.STARTED_COOKING_DATE.toString(),
                                    new Date());
                }
            } else {
                startedPraparing = OrdersManage.deliveryOrdersToCook.get(0)
                        .getDaty().get(Status.STARTED_COOKING_DATE.toString()).getTime();
            }

            diferentCooking = true;

            currentTime = new Date().getTime();

            finisTime = ((OrdersManage.deliveryOrdersToCook.get(0).getOrderedItems().size() * prepareTime) /
                    PersonelManage.listaKucharzy.size()) + startedPraparing;

            if (currentTime > finisTime) {
                OrdersManage.deliveryOrdersToCook.get(0).setStatus(Status.GOTOWE.toString());
                OrdersManage.deliveryOrdersToCook.get(0).setCookedDate(new Date(finisTime));
                OrdersManage.deliveryOrdersForDelivery.add(OrdersManage.deliveryOrdersToCook.get(0));
                OrdersManage.deliveryOrdersToCook.remove(0);
                lastOrderCooked = finisTime;
                diferentCooking = false;
                startProcess();

                if (!OrdersManage.deliveryOrdersToCook.isEmpty()) {
                    OrdersManage.deliveryOrdersToCook.get(0).setSTARTEDCOOKING(new Date(finisTime));
                    startProcess();
                }

            }
        }



    }
    public static void delivery() {

        sort();

        long starteddelivery;
        long currenttime;
        long finishtime;

        for (int i = 0; i < PersonelManage.listaDostawcow.size(); i++) {
            if (!PersonelManage.listaDostawcow.get(i).ordersToDelivery.isEmpty()) {
                PersonelManage.listaDostawcow.get(i).ordersToDelivery =
                        (ArrayList<Order>) PersonelManage.listaDostawcow.get(i).ordersToDelivery.stream()
                                .sorted(Comparator.comparing
                                        (Order -> Order.getDaty().get(Status.FINISHED_COOKING_DATE.toString()).getTime()))
                                .collect(Collectors.toList());

                if (lastOrderDelivered != 0) {
                    if (lastOrderDelivered <
                            PersonelManage.listaDostawcow.get(i).ordersToDelivery.get(0).getDaty()
                                    .get(Status.FINISHED_COOKING_DATE.toString()).getTime()) {

                        PersonelManage.listaDostawcow.get(i).ordersToDelivery.get(0)
                                .getDaty().put(Status.STARTED_DELIVERY_DATE.toString(),
                                        PersonelManage.listaDostawcow.get(i).ordersToDelivery.get(0)
                                                .getDaty().get(Status.FINISHED_COOKING_DATE.toString()));

                        starteddelivery = PersonelManage.listaDostawcow.get(i).ordersToDelivery.get(0)
                                .getDaty().get(Status.FINISHED_COOKING_DATE.toString()).getTime();

                    } else {
                        PersonelManage.listaDostawcow.get(i).ordersToDelivery.get(0)
                                .getDaty().put(Status.STARTED_DELIVERY_DATE.toString(),
                                        new Date(lastOrderDelivered));
                        starteddelivery = lastOrderDelivered;
                    }
                } else {
                    PersonelManage.listaDostawcow.get(i).ordersToDelivery.get(0)
                            .getDaty().put(Status.STARTED_DELIVERY_DATE.toString(),
                                    PersonelManage.listaDostawcow.get(i).ordersToDelivery.get(0)
                                            .getDaty().get(Status.FINISHED_COOKING_DATE.toString()));

                    starteddelivery = PersonelManage.listaDostawcow.get(i).ordersToDelivery.get(0)
                            .getDaty().get(Status.FINISHED_COOKING_DATE.toString()).getTime();
                }


                currenttime = new Date().getTime();

                finishtime = starteddelivery + deliverytime;

                if (currenttime > finishtime) {


                    /*napiwek*/
                    long totalTime = finishtime - PersonelManage.listaDostawcow.get(i).ordersToDelivery.get(0)
                            .getDaty().get(Status.ORDERED_DATE.toString()).getTime();
                    double totalprice = PersonelManage.listaDostawcow.get(i).ordersToDelivery
                            .get(0).getTotalPrice();
                    long fiftenmin = 900_000;
                    double maxTipValue;
                    maxTipValue = totalprice * (10.0/100.0);
                    if ((double)(fiftenmin/totalTime) * 2 > maxTipValue) {
                        PersonelManage.listaDostawcow.get(i).setTips(maxTipValue);
                        System.out.println(PersonelManage.listaDostawcow.get(i).getTips());
                    } else {
                        PersonelManage.listaDostawcow.get(i).setTips((double)(fiftenmin/totalTime) * 2);
                        System.out.println(PersonelManage.listaDostawcow.get(i).getTips());
                    }



                    PersonelManage.listaDostawcow.get(i).ordersToDelivery.get(0)
                            .setStatus(Status.DOSTARCZONE.toString());

                    PersonelManage.listaDostawcow.get(i).ordersToDelivery.get(0)
                            .setDeliveredDate(new Date(finishtime));

                    PersonelManage.listaDostawcow.get(i).deliveredCountIncrease();

                    completedOrders.add(PersonelManage.listaDostawcow.get(i).ordersToDelivery.get(0));

                    PersonelManage.listaDostawcow.get(i).ordersToDelivery.remove(0);

                    lastOrderDelivered = finishtime;
                    startProcess();

                }

            }
        }
    }
    public static void place() {

        sort();

        long czasObslugi = 5000;

        long starteddelivery;
        long currenttime;
        long finishtime;

        for (int i = 0; i < PersonelManage.listaKelnerow.size(); i++) {

            if (!PersonelManage.listaKelnerow.get(i).ordersToPlace.isEmpty()) {

                /*sortuje zamowienia pod wzgledem czasu zakonczenia gotowania*/
                PersonelManage.listaKelnerow.get(i).ordersToPlace =
                        (ArrayList<Order>) PersonelManage.listaKelnerow.get(i).ordersToPlace.stream()
                                .sorted(Comparator.comparing
                                        (Order -> Order.getDaty().get(Status.FINISHED_COOKING_DATE.toString()).getTime()))
                                .collect(Collectors.toList());


                if (lastOrderPlaced != 0) {
                    if (lastOrderPlaced <
                            PersonelManage.listaKelnerow.get(i).ordersToPlace.get(0).getDaty()
                                    .get(Status.FINISHED_COOKING_DATE.toString()).getTime()) {

                        PersonelManage.listaKelnerow.get(i).ordersToPlace.get(0)
                                .getDaty().put(Status.STARTED_DELIVERY_DATE.toString(),
                                        PersonelManage.listaKelnerow.get(i).ordersToPlace.get(0)
                                                .getDaty().get(Status.FINISHED_COOKING_DATE.toString()));

                        starteddelivery = PersonelManage.listaKelnerow.get(i).ordersToPlace.get(0)
                                .getDaty().get(Status.FINISHED_COOKING_DATE.toString()).getTime();

                    } else {
                        PersonelManage.listaKelnerow.get(i).ordersToPlace.get(0)
                                .getDaty().put(Status.STARTED_DELIVERY_DATE.toString(),
                                        new Date(lastOrderPlaced));
                        starteddelivery = lastOrderPlaced;
                    }
                } else {
                    PersonelManage.listaKelnerow.get(i).ordersToPlace.get(0)
                            .getDaty().put(Status.STARTED_DELIVERY_DATE.toString(),
                                    PersonelManage.listaKelnerow.get(i).ordersToPlace.get(0)
                                            .getDaty().get(Status.FINISHED_COOKING_DATE.toString()));

                    starteddelivery = PersonelManage.listaKelnerow.get(i).ordersToPlace.get(0)
                            .getDaty().get(Status.FINISHED_COOKING_DATE.toString()).getTime();
                }


                currenttime = new Date().getTime();

                finishtime = starteddelivery + czasObslugi;

                if (currenttime > finishtime) {

                    /*Napiwek*/
                    long totalTime = finishtime - PersonelManage.listaKelnerow.get(i).ordersToPlace.get(0)
                            .getDaty().get(Status.ORDERED_DATE.toString()).getTime();
                    double totalprice = PersonelManage.listaKelnerow.get(i).ordersToPlace
                            .get(0).getTotalPrice();
                    long fiftenmin = 900_000;
                    double maxTipValue;
                    maxTipValue = totalprice * (10.0/100.0);

                    if ((double)(fiftenmin/totalTime) * 2 > maxTipValue) {
                        PersonelManage.listaKelnerow.get(i).setTips(maxTipValue);
                    } else {
                        PersonelManage.listaKelnerow.get(i).setTips((double)(fiftenmin/totalTime) * 2);
                    }


                    PersonelManage.listaKelnerow.get(i).ordersToPlace.get(0)
                            .setDeliveredDate(new Date(finishtime));
                    PersonelManage.listaKelnerow.get(i).ordersToPlace.get(0)
                                    .setStatus(Status.DOSTARCZONE.toString());

                    PersonelManage.listaKelnerow.get(i).deliveredCountIncrease();
                    completedOrders.add(PersonelManage.listaKelnerow.get(i).ordersToPlace.get(0));

                    PersonelManage.listaKelnerow.get(i).ordersToPlace.remove(0);

                    lastOrderPlaced = finishtime;

                    startProcess();

                }

            }
        }
    }


    /*klasa*/
    public static class Order {

        static AtomicInteger orderID = new AtomicInteger();
        private final int id;

        private final Date orderDate;

        private final HashMap <String, Date> daty;
        private final Customer customer;
        private final boolean isDelivery;
        private String status;
        private final ArrayList <MenuManage.MenuItem> orderedItems;
        private int table;
        private double totalPrice;

        public Order(boolean isDelivery, String status, Customer customer, ArrayList<MenuManage.MenuItem> orderedItems, double totalprice) {

            id = orderID.incrementAndGet();
            this.customer = customer;
            this.isDelivery = isDelivery;
            this.status = status;
            this.orderedItems = orderedItems;
            this.orderDate = new Date();
            this.daty = new HashMap<>();
            this.totalPrice = totalprice;

            daty.put(Status.ORDERED_DATE.toString(), orderDate);

        }

        public HashMap<String, Date> getDaty() {
            return daty;
        }
        public long cookedtime;

        public int getId() {
            return id;
        }

        public void priceDiscount() {
            this.totalPrice *= (double) 80/100;
        }

        public double getTotalPrice() {
            return totalPrice;
        }

        public void setCookedDate(Date date) {
            daty.put(Status.FINISHED_COOKING_DATE.toString(), date);
        }


        public void setSTARTEDCOOKING(Date date) {
            daty.put(Status.STARTED_COOKING_DATE.toString(), date);
        }
        public void setDeliveredDate(Date date) {
            daty.put(Status.FINISHED_DELIVERY_DATE.toString(), date);
        }
        public void setStartingDelivery(Date date) {
            daty.put(Status.STARTED_DELIVERY_DATE.toString(), date);
        }



        public void setStatus(String status) {
            this.status = status;
        }

        public void setCookedtime(long cookedtime) {
            this.cookedtime = cookedtime;
        }

        public long getCookedtime() {
            return cookedtime;
        }

        public Customer getCustomer() {
            return customer;
        }
        public boolean isDelivery() {
            return isDelivery;
        }
        public String getStatus() {
            return status;
        }
        public ArrayList<MenuManage.MenuItem> getOrderedItems() {
            return orderedItems;
        }
    }
}





