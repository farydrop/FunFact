package com.example.funfact.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.funfact.R
import com.example.funfact.model.FactsModel

class MainViewModel : ViewModel() {

    val factsState = MutableLiveData<ArrayList<FactsModel>>()
    private val data = ArrayList<FactsModel>()

    init {
        data.add(
            FactsModel(
                R.drawable.car,
                "F1 racers lose over 3 liters of body water during 1 race",
                "According to Telegraph, the average F1 is equipped with 1.5 liters of water in the car, which is hooked to the helmet. This, in most cases, isn't enough to keep the drivers hydrated for long enough. It thus takes a lot of endurance and physical fitness to be a Formula race car driver."
            )
        )
        data.add(
            FactsModel(
                R.drawable.pic_1,
                "Car tires lose 0.5kgs during a race",
                "Formula 1 car tires lose 0.5 kgs during the race. This is because of the wear and tear that comes as a result of the high speed and the abrupt breaking. The G-Force doesn't spare the tires."
            )
        )
        data.add(
            FactsModel(
                R.drawable.pic_2,
                "The F1 Helmets are the toughest in the world",
                "F1 helmets must be extremely light. This presents the challenge of coming up with a product that can also be as tough as it needs to be. To ensure that the helmets meet the strict requirements, they have to go through a couple of fragmentation and deformation tests. Carbon fiber is the main material that's used to make Formula 1 helmets because of its robustness."
            )
        )
        data.add(
            FactsModel(
                R.drawable.pic_3,
                "The average F1 racer loses 4 kg during a race",
                "It’s not just tires, but also racers who lose weight during an F1 race. The main reason why Formula 1 drivers lose up to 4 KGs is the unbearable temperatures in the cockpit, which can reach up to 50 degrees Celsius."
            )
        )
        data.add(
            FactsModel(
                R.drawable.pic_4,
                "Weight of an F1 car must be over 728kg (without fuel)",
                "According to Wikipedia, the 728 KG permissible weight includes the driver but not when the car has fuel."
            )
        )
        data.add(
            FactsModel(
                R.drawable.pic_5,
                "The lifespan of an F1 engine is less than 5 races",
                "A normal Formula 1 engine can't last more than 5 races. The participants set aside a sizable budget just for the development of the engine. They are engineered to get the best out of them, even if it means functioning just for a few hours. The high level of precision that goes into building the engines means they're more subject to wear and tear."
            )
        )
        data.add(
            FactsModel(
                R.drawable.pic_6,
                "Pit stops take less than 3 seconds",
                "The average F1 crew takes about 3.0 seconds to change the tires. This is important because constructors are also gauged at the end of the season. For the driver, it means he'll focus more on the time to complete the race."
            )
        )
        data.add(
            FactsModel(
                R.drawable.pic_7,
                "F1 cars rev at 15,000 RPM",
                "A normal vehicle can achieve up to 6,000 RPM, while a Formula 1 race car can achieve twice as much. According to Wikipedia, this is made possible because of the power that's produced by the engine. The naturally aspirated engines for Formula 1 cars haven't changed over the years and have been consistent with the output."
            )
        )
        data.add(
            FactsModel(
                R.drawable.pic_8,
                "46 F1 racers have lost their lives so far",
                "As much as Formula 1 cars are among the safest in the world, there are some calamities drivers can't avoid. There have been a total of 46 recorded deaths as a result of Formula 1 car accidents. The oldest driver to have died was 50-year-old Chet Miller, while the youngest was Ricardo Rodriguez, who was 20."
            )
        )
        factsState.value = data
    }
}