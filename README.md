<a id="readme-top"></a>

[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]

<br />
<div align="center">
  <a href="https://github.com/Z3belek/Anima">
    <img src="logo.png" alt="Logo" width="90" height="80">
  </a>
  <h3 align="center">Anima</h3>
  <p align="center">
    Una nueva forma de ver anime
    <br />
    <a href="https://github.com/Z3belek/Anima/releases">Releases</a>
    &middot;
    <a href="https://github.com/Z3belek/Anima/issues/new?labels=bug&template=bug-report---.md">Report Bug</a>
    &middot;
    <a href="https://github.com/Z3belek/Anima/issues/new?labels=enhancement&template=feature-request---.md">Request Feature</a>
  </p>
</div>
<details>
  <summary><strong>Tabla de Contenidos</strong></summary>
  <ol>
    <li>
      <a href="#acerca-del-proyecto">Acerca del Proyecto</a>
      <ul>
        <li><a href="#construido-con">Construido con</a></li>
      </ul>
    </li>
    <li>
      <a href="#construccion-y-pruebas">Construccion y pruebas</a>
      <ul>
        <li><a href="#prerrequisitos">Prerrequisitos</a></li>
        <li><a href="#instalacion">Instalacion</a></li>
      </ul>
    </li>
    <li><a href="#uso">Uso</a></li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#contribuciones">Contribuciones</a></li>
  </ol>
</details>

## Acerca del Proyecto

[![Home Screen][app-screenshot-one]](https://example.com)

Anima es una aplicacion para Android y Google TV, realizada con compose, y pensada para la reproduccion de contenido multimedia. La aplicacion de momento cuenta con un sistema simple pero efectivo, en el cual se puede reproducir el contenido desde una amplia biblioteca de contenido, sin publicidad, y con una interfaz amigable y facil de usar.

Algunas caracteristicas de la aplicacion son:

- Reproductor sin publicidad
- Puedes continuar viendo desde donde lo dejaste
- Y pronto se agregaran mas funciones como la posibilidad de crear listas de reproduccion, y un sistema de recomendaciones. :smile:

[![Video Player Screen][app-screenshot-two]](https://example.com)

La aplicacion es completamente gratuita, y pronto espero poder publicar una version para celulares, y tablets. Mientras tanto puedes disfrutar de la version para Google TV, y Android TV.

Por supuesto sientete libre de contribuir al proyecto, ya sea reportando errores, o sugiriendo nuevas funciones, o incluso creando nuevas funciones.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

### Construido con

La aplicacion fue construida con Kotlin, y usando como IDE Android Studio, pensada principalmente para la compatibilidad con la mayoria de dispositivos Android, y Google TV, y su fluidez en la reproduccion de contenido multimedia.

- [![Kotlin][Kotlin]][Kotlin-url]
- [![Android][Android]][Android-url]

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Construccion y pruebas

Si quieres contribuir al proyecto, o simplemente quieres probar la aplicacion, puedes hacerlo siguiendo los siguientes pasos:

### Prerrequisitos

Es recomendable tener instalado la version mas reciente de Android Studio, y el SDK de Android, para poder compilar la aplicacion sin problemas.

- Android Studio
  ```sh
  https://developer.android.com/studio
  ```

### Instalacion

Para poder compilar la aplicacion, solo necesitas seguir los siguientes pasos:

1. Clonar el repositorio
   ```sh
    git clone github.com/Z3belek/Anima.git
   ```
2. Asegúrate de tener configurado Android Studio con el SDK de Android apropiado.
3. Crea un archivo `local.properties` en la raíz del proyecto con las siguientes claves si vas a conectarte a una instancia de Supabase:
   ```properties
   SUPABASE_URL=TU_URL_DE_SUPABASE
   SUPABASE_ANON_KEY=TU_CLAVE_ANON_DE_SUPABASE
   ```
4. Asegurate de que tu tabla, coincida con las entidades de la aplicacion.
5. Usa el emulador de Android Studio, o un dispositivo fisico para probar la aplicacion.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Uso

El uso de la aplicacion es bastante intuitivo, y no requiere de una configuracion previa. Sin embargo si en proximas actualizaciones se requiere de una configuracion previa, sera declarada aca.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Roadmap

- [x] Screens basicas creadas
- [x] Selector de fuente en el reproductor
- [x] Funcion para continuar viendo desde donde lo dejaste
- [ ] Recomendaciones o relacionados
- [ ] Segmentacion del contenido
- [ ] Sistema de perfiles
- [ ] Listas del usuario
- [ ] Integracion con MAL
- [ ] Mejoras en la UI

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Contribuciones

Las contribuciones son lo que hacen que la comunidad de código abierto sea un lugar increíble para aprender, inspirarse y crear. Cualquier contribución que hagas será **muy apreciada**.

Si quieres contribuir al proyecto, puedes hacerlo siguiendo los siguientes pasos:

1. Haz un Fork del Proyecto
2. Crea tu Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Realiza tus cambios y haz un commit (`git commit -m 'Add some AmazingFeature'`)
4. Haz un Push a la Rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

[forks-shield]: https://img.shields.io/github/forks/Z3belek/Anima.svg?style=for-the-badge
[forks-url]: https://github.com/Z3belek/Anima/network/members
[stars-shield]: https://img.shields.io/github/stars/Z3belek/Anima.svg?style=for-the-badge
[stars-url]: https://github.com/Z3belek/Best-README-Template/stargazers
[issues-shield]: https://img.shields.io/github/issues/Z3belek/Anima.svg?style=for-the-badge
[issues-url]: https://github.com/Z3belek/Anima/issues
[app-screenshot-one]: screenshot-one.png
[app-screenshot-two]: screenshot-two.png
[Kotlin]: https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=Kotlin&logoColor=white
[Kotlin-url]: https://kotlinlang.org/
[Android]: https://img.shields.io/badge/android%20studio-3DDC84?style=for-the-badge&logo=android%20studio&logoColor=white
[Android-url]: https://developer.android.com/
