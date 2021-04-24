import { Client, Collection } from 'discord.js';
import { connect } from 'mongoose';
import path from 'path';
import { readdirSync } from 'fs';
import { Command, Event, Config } from '../Interfaces';
import ConfigJson from '../config.json';
import chalk from 'chalk';

class ExtendedClient extends Client {
    public commands: Collection<string, Command> = new Collection();
    public events: Collection<string, Event> = new Collection();
    public config: Config = ConfigJson;
    public aliases: Collection<string, Command> = new Collection();
    public cooldowns: Collection<string, number> = new Collection();

    public async init() {

        // Login and mongo connection

        connect(this.config.mongo_url, {
            useUnifiedTopology: true,
            useNewUrlParser: true,
            useFindAndModify: true
        }).then(() => console.log('[' + chalk.magenta('Database') + ']' + 'Connected to MongoDB!'))

        this.login(this.config.token);

        // Registering commands

        const commandPath = path.join(__dirname, '..', "Commands");
        readdirSync(commandPath).forEach((dir) => {
            const commands = readdirSync(`${commandPath}/${dir}`).filter((file) => file.endsWith('.ts'));

            for (const file of commands) {
                const { command } = require(`${commandPath}/${dir}/${file}`);
                this.commands.set(command.name, { cooldown: 3, ...command});

                if (command.aliases) {
                    if (command.aliases.length) {
                        command.aliases.forEach((alias) => {
                            this.aliases.set(alias, command);
                        })
                    }
                }
                console.log('[' + chalk.green("Commands") + ']' + ` Registering ${file}`);
            }
        })

        // Registering events

        const eventPath = path.join(__dirname, '..', "Events");
        readdirSync(eventPath).forEach(async (file) => {
            const { event } =  await import(`${eventPath}/${file}`);
            this.events.set(event.name, event);
            this.on(event.name, event.run.bind(null, this));

            console.log('[' + chalk.blueBright('Events') + ']' + `Registering ${file}`);
        })
    }
}

export default ExtendedClient;