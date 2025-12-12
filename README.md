# FancyMenuWorldRenderer

An addon for FancyMenu that allows the user to create 3D scenes using blocks

## Description

The user can define custom 3D scenes using blocks, and then they can be renderer as part of FancyMenu's custom UI. Then, the scene can be rotated, scaled and translated using the editor. It also allows to use placeholders to animate these parameters.

## How to use it

### Define the scene

The scene is defined as a json file. An easy way to create the scene is to go into a world and use the command ```fmsr-create```.
This command has two arguments and a third optional argument. The first argument is the position of the first corner of the scene, the second argument is the second corner. Like this:

```fmsr-create 0 0 0 1 1 1```

Once you execute this command, a message will be displayed in the chat confirming the creation, if you click the blue text, the json scene should copy to your clipboard. If you decide to use the third argument, the result will be outputed to a file:

```fmsr-create 0 0 0 1 1 1 scene.json```

The file will be created inside FancyMenu's config folder

### Use the scene inside fancy menu

There will be a new element in FancyMenu with the name *Scene*. When you right-click the element there should be an option to choose between two input options: File and Direct. The first option allows you to use the contents of a file, for example the previously created *scene.json*. The second options allows you to write directly the scene as a parameter.

### Customize the scene

When you right-click the element, there should be a list of options for setting the offset, rotation and scale of the scene. These options can be parametrized using placeholders. If you want to dynamically update them, for example to automatically change them overtime to make an animation, you should enable *Dynamic Rotation*, *Dynamic Offset* or *Dynamic Scale* options inside the right-click context menu.

## TODO
- Render entities
- 3D models
- Particle rendering

A way of imitating entities and models, is to create a custom resourcepack that adds custom models and using those blocks as part of the scene.