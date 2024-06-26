import java.io.IOException;
import java.util.ArrayList; //для динамического массива
import java.util.InputMismatchException;
import java.util.Scanner; //для сканера
import java.util.logging.Level;

public class Factory { //основной класс
    public static void main(String[] args) throws IOException { //основной блок кода. без него программа работать не будет
        Scanner scanner = new Scanner(System.in); //создание сканера
        ArrayList<Software> wares = new ArrayList<>(); //динамический массив в данными либо о ПО, либо об АС
        String addProducts = "да"; //для добавления продуктов

        System.out.println("ДОБРО ПОЖАЛОВАТЬ НА КОМПЬЮТЕРНЫЙ ЗАВОД");

        do
        {
            System.out.println("---------------------------------------");
            System.out.println("Вы рассматриваете: \n1. ПО \n2. Аппаратное средство ?");
            char option = scanner.next().charAt(0); //переменная для выбора опции

            String proceed = "да"; //по умолчанию стоит "да", чтобы будущие циклы смогли запуститься
            switch(option)
            {
                case '1': //ПО
                    do
                    {
                        SoftWare(scanner, wares); //создание ПО
                        System.out.println("Добавить еще одно ПО? (да/нет)");
                        proceed = scanner.next();
                    } while(proceed.equalsIgnoreCase("да")); //цикл будет продолжаться до тех пор, пока не будет написано что-то помимо "да"
                    break;

                case '2': //аппаратное обеспечение
                    String compName; //для названия аппаратного средства
                    double compPrice = 0; //для предварительной цены АС
                    String isSoftware; //проверка, есть ли ПО в АС

                    do
                    {
                        System.out.println("Введите название аппаратного средства.");
                        compName = scanner.next();
                        try
                        {
                            System.out.println("Введите предварительную цену аппаратного обеспечения.");
                            compPrice = scanner.nextDouble();
                        }
                        catch(InputMismatchException e)
                        {
                            Log hardwareLog = new Log("hardware logs.txt");
                            hardwareLog.logger.log(Level.SEVERE, "Введено не число.");
                        }

                        System.out.println("В него встроено ПО? (да/нет)");
                        isSoftware = scanner.next();

                        if (isSoftware.equalsIgnoreCase("да"))
                        {
                            Products hardWare = new Products(compName, compPrice, true); //создание АС
                            SoftWare(hardWare, scanner, wares); //привязка АС к ПО
                        }
                        else
                        {
                            Software hardWare = new Software(compName, compPrice, false, false); //создание АС без привязки к ПО
                            wares.add(hardWare); //добавление созданного АС в созданный раннее массив (отведенный для продуктов)
                        }
                        System.out.println("Добавить еще одно аппаратное средство? (да/нет)");
                        proceed = scanner.next();
                    } while(proceed.equalsIgnoreCase("да")); //цикл будет продолжаться до тех пор, пока не будет написано что-то помимо "да"
                    break;
                default:
                    System.out.println("Такой опции нет.");
                    break;
            }
            System.out.println("Добавить еще один продукт? (да/нет)");
            addProducts = scanner.next();
        } while (addProducts.equalsIgnoreCase("да"));

        try
        {
            System.out.println("-----------------------------------");
            double cost = Products.calculateTotalCost(Software.SoftwareCopy.PricesList(wares)); //расчет общей цены
            System.out.println("Общая цена: " + cost);

            cost = Products.calculateAverageCost(Software.SoftwareCopy.PricesList(wares)); //расчет средней цены
            System.out.println("Средняя цена: " + cost);

            cost = Products.findMaxCost(Software.SoftwareCopy.PricesList(wares)); //расчет максимальной цены
            System.out.println("Максимальная цена: " + cost);

            System.out.println("\nСписок.");
            Software.SoftwareCopy.SoftwareInfo(wares); //вывод списка
        }
        catch(IndexOutOfBoundsException e)
        {
            Log hardwareLog = new Log("logs.txt");
            hardwareLog.logger.log(Level.WARNING, "Список пустой.");
        }

    }

    private static void SoftWare(Scanner scanner, ArrayList<Software> wares) throws IOException //метод для добавления нового ПО
    {
        String softName; //для названия ПО
        double softPrice = 0; //для предварительной цены ПО
        boolean result; //для проверки доступности обновления ПО

        System.out.println("Введите название ПО.");
        softName = scanner.next();
        System.out.println("Введите предварительную цену ПО. Если оно бесплатное, введите 0.");

        try
        {
            softPrice = scanner.nextDouble();
        }
        catch (SecurityException | InputMismatchException e)
        {
            Log softwareLog = new Log("software logs.txt");
            softwareLog.logger.log(Level.SEVERE, "Введено не число.");
        }

        result = Software.checkForUpdates(scanner);
        Software soft = new Software(softName, softPrice, true, result);

        wares.add(soft); //добавление созданного ПО в созданный раннее массив (отведенный для продуктов)

        if (soft.Update == true)
        {
            soft.downloadAndUpdate(); //обновление ПО
        }

        System.out.println("Создать резервную копию ПО? (да/нет)");
        String backup = scanner.next();

        if (backup.equalsIgnoreCase("да"))
        {
            soft.backupData(); //сохранение резервной копии ПО
        }
    }

    private static void SoftWare(Products hardWare, Scanner scanner, ArrayList<Software> wares) throws IOException //метод для добавления ПО при АС
    {
        double softPrice = 0; //для предварительной цены ПО
        boolean result; //для проверки доступности обновления ПО

        System.out.println("Введите предварительную цену ПО. Если оно бесплатное, введите 0.");

        try
        {
            softPrice = scanner.nextDouble();
            hardWare.Price += softPrice; //добавление стоимости ПО к стоимости АС
        }
        catch (SecurityException | InputMismatchException e)
        {
            Log softhardLog = new Log("hardware with software logs.txt");
            softhardLog.logger.log(Level.SEVERE, "Введено не число.");
        }

        result = Software.checkForUpdates(scanner);
        Software soft = new Software(hardWare.Name, softPrice, true, result);

        wares.add(soft); //добавление созданного ПО в созданный раннее массив (отведенный для продуктов)

        if (soft.Update == true)
        {
            soft.downloadAndUpdate(); //обновление ПО
        }

        System.out.println("Создать резервную копию проекта? (да/нет)");
        String backup = scanner.next();

        if (backup.equalsIgnoreCase("да"))
        {
            soft.backupData(); //сохранение резервной копии ПО
        }
    }
}

class Products{
    public String Name; //название продукта
    public double Price; //предварительная цена продукта

    public boolean SoftwareSupported; //поддерживает ли продукт ПО

    public Products(String name, double price, boolean isSupported) throws IOException //конструктор для создания объектов (продуктов)
    {
        this.Name = name;
        this.Price = price;
        this.SoftwareSupported = isSupported;
    }
    // Функция для расчета общей стоимости продукции на заводе
    public static double calculateTotalCost(ArrayList<Double> productCosts) {
        double totalCost = 0; //инициализация переменной, хранящей в себе общую стоимость
        for (double cost : productCosts) {
            totalCost += cost;
        }
        return totalCost;
    }

    // Функция для определения средней стоимости продукции на заводе
    public static double calculateAverageCost(ArrayList<Double> productCosts) {
        double totalCost = calculateTotalCost(productCosts); //подсчет общей стоимости
        return totalCost / productCosts.size(); //возврат средней стоимости
    }

    // Функция для определения наибольшей стоимости продукции на заводе
    public static double findMaxCost(ArrayList<Double> productCosts) {
        double maxCost = productCosts.get(0); //инициализация переменной, хранящей в себе максимальную стоимость
        for (double cost : productCosts) {
            if (cost > maxCost) { //значение ранее объявленной переменной изменится только в том случае, если значение объявленное ранее методом перебора
                maxCost = cost;
            }
        }
        return maxCost;
    }
}

class Software extends Products { //классу software передаются свойства и объекты из класса products

    public boolean Update; //переменная для обновления

    public Software(String name, double price, boolean isSupported, boolean update) throws IOException //конструктор для создания объектов
    {
        super(name, price, isSupported); //использование объектов класса products
        this.Update = update;
    }

    public static boolean checkForUpdates(Scanner scanner) { // Функция для проверки наличия обновлений ПО
        System.out.println("Есть ли обновления для установки? (да/нет)");
        String response = scanner.next();
        return response.equalsIgnoreCase("да"); //если обновления есть, возвращается true.
    }

    // Функция для загрузки и установки обновлений ПО
    public void downloadAndUpdate() throws IOException {
        System.out.println("\nЗагрузка и установка обновлений в ПО '" + Name + "'...");
        // Пример: имитация загрузки и установки
        try {
            Thread.sleep(2000); // Имитация загрузки. она проходит 2 секунды
            System.out.println("Обновления в ПО '" + Name + "' успешно установлены.\n");
        } catch (InterruptedException e) {
            Log softwareLog = new Log("software logs.txt");
            softwareLog.logger.log(Level.FINEST, "Не удалось обновить ПО."); //на всякий случай, если поток прервется
        }
    }

    // Функция для резервного копирования данных ПО
    public void backupData() throws IOException {
        System.out.println("Начало резервного копирования данных...");
        // Пример: имитация процесса резервного копирования
        try {
            Thread.sleep(3000); // Имитация процесса резервного копирования. оно проходит 3 секунды
            System.out.println("Резервное копирование завершено.\n");
        } catch (InterruptedException e) {
            Log softwareLog = new Log("software logs.txt");
            softwareLog.logger.log(Level.FINEST, "Не удалось совершить резервное копирование данных."); //на всякий случай, если поток прервется
        }
    }
    public static class SoftwareCopy {
        public static void SoftwareInfo(ArrayList<Software> wares) { //функция для вывода информации о ПО
            for (var item : wares) //перебор в массиве с ПО
            {
                System.out.println("-------------------------------------");
                if (item.SoftwareSupported == true)
                {
                    System.out.println("Название: " + item.Name + "\nПредварительная цена: " + item.Price
                            + "\nНаличие ПО: " + item.SoftwareSupported + "\nНаличие обновлений: " + item.Update);
                }
                else
                {
                    System.out.println("Название: " + item.Name + "\nПредварительная цена: " + item.Price
                            + "\nНаличие ПО: " + item.SoftwareSupported);
                }
            }
        }

        public static ArrayList<Double> PricesList(ArrayList<Software> wares) //извлечение цены из массива с ПО
        {
            ArrayList<Double> prices = new ArrayList<>(); //создание нового динамичного массива
            for (var item : wares) //перебор в массиве с ПО
            {
                prices.add(item.Price); //добавление цен в новый массив
            }
            return prices; //возвращение массива
        }
    }
}

