# CAST

The CAST (Clever Autonomous Stock Trading) project has the goal of trading as reliably, autonomously and performantly as possible with high returns. To achieve this, the latest technologies were used in combination with scientific studies. Various interfaces were created to make the applications as easy to use as possible. CAST does not want to predict stock prices, but it is about finding the optimal times to buy and sell stocks. This is a project with a promising future.

## Status

|             | Build Status                                                                                                            |
|-------------|-------------------------------------------------------------------------------------------------------------------------|
| Master      | ![Java CI with Gradle](https://github.com/LukasBreuerDE/cast/workflows/Java%20CI%20with%20Gradle/badge.svg) |

## Scientific background
In 2011, the Polish computer scientist Miroslaw Kordos published a paper entitled "A new approach to neural network based stock trading strategy". In this article, Kordos describes how neural networks can be used to develop a trading strategy. The idea of this paper was used to implement CAST. However, in many places the paper's statements were deviated from in order to improve the results.

If anyone is interested in the work of Miroslaw Kordos, you can visit his website [here](http://kordos.com/). He has also developed many other projects in other areas.

## Modules
### Core
The core module comprises basic functions on which the other modules are based. Each subsequent module is dependent on the core module. The module itself is not an executable application.

### Train
In this module, the models for the prediction of trading signals are created and trained. The DL4J library is used for this purpose. After training, the models are stored so that they can be used by other applications.

### Deploy
In the deploy module, the previously trained models are used. Every day after the market closes, an algorithm checks whether a trade should be made. If it is determined that a trade is profitable, the data is passed on to a provider who executes it.

### Access
This module is used as an interface for other applications, such as the web panel. The main purpose is to manage the databases and, if necessary, to make manual interventions. Spring Boot was used for the implementation.

## Investopedia
To reduce the risk of losses during the review phase, the Investopedia simulator is used first. This offers the possibility of trading stocks realistically based on real data. Once all applications have been perfected, Investopedia will be replaced by a real trading provider.

## License

[GPL](https://github.com/LukasBreuerDE/cast/blob/master/LICENSE.md)

