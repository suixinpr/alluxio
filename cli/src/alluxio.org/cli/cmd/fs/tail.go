/*
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */

package fs

import (
	"fmt"

	"github.com/spf13/cobra"

	"alluxio.org/cli/env"
)

var Tail = &TailCommand{
	BaseJavaCommand: &env.BaseJavaCommand{
		CommandName:   "tail",
		JavaClassName: "alluxio.cli.fs.FileSystemShell",
		Parameters:    []string{"tail"},
	},
}

type TailCommand struct {
	*env.BaseJavaCommand

	bytes string
}

func (c *TailCommand) Base() *env.BaseJavaCommand {
	return c.BaseJavaCommand
}

func (c *TailCommand) ToCommand() *cobra.Command {
	cmd := c.Base().InitRunJavaClassCmd(&cobra.Command{
		Use:   fmt.Sprintf("%v [path]", Tail.CommandName),
		Short: "Print the trailing bytes from the specified file",
		Args:  cobra.ExactArgs(1),
		RunE: func(cmd *cobra.Command, args []string) error {
			return c.Run(args)
		},
	})
	cmd.Flags().StringVar(&c.bytes, "bytes", "", "Byte size to print")
	return cmd
}

func (c *TailCommand) Run(args []string) error {
	var javaArgs []string
	if c.bytes != "" {
		javaArgs = append(javaArgs, "-c", c.bytes)
	}
	javaArgs = append(javaArgs, args...)
	return c.Base().Run(javaArgs)
}